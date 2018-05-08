package Image;

import com.sun.istack.internal.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static java.lang.Math.atan;
import static java.lang.Math.pow;
import static java.lang.StrictMath.sqrt;

public class ImageClass {
    private int sobelX[][]={{-1,0,1},{-2,0,2},{-1,0,1}};
    private int sobelY[][]={{-1,-2,-1},{0,0,0},{1,2,1}};
    public BufferedImage getImage(String image){
        int[] rgb = new int[3];
        File file = new File(image);
        BufferedImage bi = null;
        try {
            bi = ImageIO.read(file);
        } catch (IOException e) {

            e.printStackTrace();
        }
        int width = bi.getWidth();
        int height = bi.getHeight();
        int minX = bi.getMinX();
        int minY = bi.getMinY();
        for(int y = minY; y < height; y++) {
            for(int x = minX; x < width; x++) {
                //获取包含这个像素的颜色信息的值, int型
                int pixel = bi.getRGB(x, y);
                //从pixel中获取rgb的值
                rgb[0] = (pixel & 0xff0000) >> 16; //r
                rgb[1] = (pixel & 0xff00) >> 8; //g
                rgb[2] = (pixel & 0xff); //b
            }
        }
        return bi;
    }

    /**
     *
     * @param x 获取图片RGB的位置的x坐标
     * @param y 获取图片RGB的位置的y坐标
     * @param image 需要获取的图片
     * @return 返回的RGB数组[0]-r,[1]-g,[2]-b
     */
    private int[] getRGB(int x, int y, BufferedImage image) {
        int rgb[]=new int[3];
        int pixel1 = image.getRGB(x, y);
        rgb[0]=(pixel1 >> 16) & 0xff;
        rgb[1] = (pixel1 >> 8) & 0xff;
        rgb[2]= pixel1 & 0xff;
        return rgb;
    }

    /**
     * @param image 需要设置RGB值的对象
     * @param x 图片修改位置的x坐标
     * @param y 图片修改位置的y上坐标
     * @param  srcPixel 原像素值
     * @param  rgb 修改后的rgb值
     */
    private void setRGB(BufferedImage image,int x,int y,int srcPixel,int rgb[]){
        int pixel;
        if(rgb[0]>255)
            rgb[0]=255;
        else if(rgb[0]<0)
            rgb[0]=0;
        if(rgb[1]>255)
            rgb[1]=255;
        else if(rgb[1]<0)
            rgb[1]=0;
        if(rgb[2]>255)
            rgb[2]=255;
        else if(rgb[2]<0)
            rgb[2]=0;
        pixel = (rgb[0] << 16) & 0x00ff0000 | (srcPixel & 0xff00ffff);
        pixel = (rgb[1]  << 8) & 0x0000ff00 | (pixel & 0xffff00ff);
        pixel = (rgb[2] ) & 0x000000ff | (pixel & 0xffffff00);
        image.setRGB(x, y, pixel);
    }

    public BufferedImage toGray(BufferedImage srcImage,String  savePath) {
        int width = srcImage.getWidth();
        int height = srcImage.getHeight();
        int rgb[];
        BufferedImage grayImage=new BufferedImage(width,height,BufferedImage.TYPE_BYTE_GRAY);
        try {
            for(int y = srcImage.getMinY(); y < height; y++) {
                for(int x = srcImage.getMinX(); x < width ; x ++) {
                    rgb=getRGB(x,y,srcImage);
                    //加权法的核心,加权法是用图片的亮度作为灰度值的
                    int grayValue = (rgb[0]*299 + rgb[1]*587 + rgb[2]*114 + 500) / 1000;
                    rgb[0]=rgb[1]=rgb[2]=grayValue;
                    setRGB(grayImage,x,y,srcImage.getRGB(x, y),rgb);

                }
            }
            ImageIO.write(grayImage, "jpg", new File(savePath));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return  grayImage;
    }
    public BufferedImage gaussianPicture(BufferedImage image, int [][]temp, int modulusOfNormalization, String savePath){

        //保存高斯模糊的结果
        BufferedImage gsImage=new BufferedImage(image.getWidth(), image.getHeight(),image.getType());

        switch (temp.length){
            //使用一维高斯模板两次卷积 O(n*M*N)
            case 1:
                System.out.println("1D");
                Convolution(image,gsImage,temp[0],modulusOfNormalization);
                break;
            //使用二维高斯模板一次卷积 O(n*n*M*N)
            default:
                System.out.println("2D");
                Convolution(image,gsImage,temp,modulusOfNormalization);
                break;
        }
        try {
            File file=new File(savePath+"gaussian.jpg");
            ImageIO.write(gsImage, "jpg", file);
        }catch (Exception e){
            e.printStackTrace();
        }

        return  gsImage;
    }
    public BufferedImage sobelPicture(BufferedImage srcImage,String savePath){
        int width = srcImage.getWidth();
        int height = srcImage.getHeight();
        int modulusOfNormalization=1;
        BufferedImage soble[]=new BufferedImage[2];

        soble[0]=new BufferedImage(width,height,srcImage.getType());
        soble[1]=new BufferedImage(width,height,srcImage.getType());
        BufferedImage gradImage=new BufferedImage(width,height,soble[0].getType());
        BufferedImage angleImage=new BufferedImage(width,height,soble[0].getType());

        Convolution(srcImage,soble[0],sobelX,modulusOfNormalization);
        Convolution(srcImage,soble[1],sobelY,modulusOfNormalization);

        gradPicture(soble,gradImage);

        anglePicture(soble, angleImage);

        try {
            ImageIO.write(soble[0], "jpg", new File(savePath+"Gx.jpg"));
            ImageIO.write(soble[1], "jpg", new File(savePath+"Gy.jpg"));
            ImageIO.write(gradImage, "jpg", new File(savePath+"Grad.jpg"));
            ImageIO.write(angleImage, "jpg", new File(savePath+"Angle.jpg"));
        }catch (Exception e){
            e.printStackTrace();
        }
        return gradImage;
    }

    private void gradPicture(BufferedImage[] soble, BufferedImage outImage) {
        int xStart=soble[0].getMinX();
        int yStart=soble[0].getMinY();
        int width = soble[0].getWidth();
        int height = soble[0].getHeight();
        int rgb0[],rgb1[],rgb[]=new int[3];
        for(int x=xStart;x<width;x++){
            for(int y=yStart;y<height;y++){
                rgb0=getRGB(x,y,soble[0]);
                rgb1=getRGB(x,y,soble[1]);
                for(int i=0;i<3;i++){
                    rgb[i]=(int) sqrt(pow(rgb0[i],2)+pow(rgb1[i],2));
                }
                setRGB(outImage,x,y,soble[0].getRGB(x,y),rgb);
            }
        }
    }
    private void anglePicture(BufferedImage[] soble,BufferedImage outImage){
        int xStart=soble[0].getMinX();
        int yStart=soble[0].getMinY();
        int width = soble[0].getWidth();
        int height = soble[0].getHeight();
        int rgb0[],rgb1[],rgb[]=new int[3];
        for(int x=xStart;x<width;x++){
            for(int y=yStart;y<height;y++){
                rgb0=getRGB(x,y,soble[0]);
                rgb1=getRGB(x,y,soble[1]);
                int max=0;
                for(int i=0;i<3;i++){
                    if(rgb0[i]==0){
                        rgb0[i]+=1;
                    }
                    rgb0[i]=(int) atan(rgb1[i]/rgb0[i]);
                    if(i==0){
                        max=rgb[i];
                    }
                    max=rgb[i]>max?rgb[i]:max;
                    rgb[i]=max;
                }
                setRGB(outImage,x,y,soble[0].getRGB(x,y),rgb);
            }
        }
    }

    private double Int(double x) {
        if(x>=0)
            return (int)x;
        else
            return (int)(x-1);
    }


    private void  Convolution( BufferedImage srcImage, BufferedImage outImage, int Convolution[][], int modulusOfNormalization){
        int xStart=srcImage.getMinX();
        int yStart=srcImage.getMinY();
        int width = srcImage.getWidth();
        int height = srcImage.getHeight();
        int border,center;
        border=center=(Convolution[0].length-1)/2;
        int rgb[]=new int[3];
        for(int x = xStart; x < width ; x ++) {
            for(int y = yStart; y < height; y++) {
                rgb[0]=rgb[1]=rgb[2]=0;//r=g=b=0
                for(int i=-border;i<=border;i++){
                    int x1=x+i;
                    int[] rgb1;
                    for(int j=-border;j<=border;j++){
                        int y1=y+j;
                        if(x1<xStart){
                            x1=xStart;
                        }else if(x1>=width){
                            x1=width-1;
                        }
                        if(y1<yStart){
                            y1=yStart;
                        }else if(y1>=height){
                            y1=height-1;
                        }
                        rgb1=getRGB(x1,y1,srcImage);
                        //-----------------------------------
                        rgb[0]+=(Convolution[i+center][j+center]*rgb1[0]);
                        rgb[1]+=(Convolution[i+center][j+center]*rgb1[1]);
                        rgb[2]+=(Convolution[i+center][j+center]*rgb1[2]);
                        //------------------------------------
                    }
                }
                try{
                    rgb[0]/=modulusOfNormalization;
                    rgb[1]/=modulusOfNormalization;
                    rgb[2]/=modulusOfNormalization;
                }catch (Exception e){
                    e.printStackTrace();
                }
                setRGB(outImage,x,y,srcImage.getRGB(x,y),rgb);
            }//遍历y
        }//遍历x
    }
    private void Convolution(BufferedImage srcImage,BufferedImage outImage,int Convolution[],int modulusOfNormalization){
        int xStart=srcImage.getMinX();
        int yStart=srcImage.getMinY();
        int width = srcImage.getWidth();
        int height = srcImage.getHeight();
        int border,center;
        border=center=(Convolution.length-1)/2;
        int rgb[]=new int[3];
        BufferedImage outImage_temp=new BufferedImage(width, height,srcImage.getType());
        int timeXY[][]={{xStart,width},{yStart,height}};
        for(int time=0;time<2;time++){//两次卷积

            for(int firstXY=timeXY[time][0]; firstXY<timeXY[time][1]; firstXY++){
                int secondStart=timeXY[time==0?1:0][0];
                int secondEnd=timeXY[time==0?1:0][1];

                for(int secondXY=secondStart; secondXY<secondEnd; secondXY++){
                    int x=(time==0?firstXY:secondXY);
                    int y=(time==1?firstXY:secondXY);
                    rgb[0]=rgb[1]=rgb[2]=0;//r=g=b=0

                    for(int i=-border;i<=border;i++){
                        int xy1=secondXY+i;
                        int rgb1[];
                        if (xy1<secondStart){
                            xy1=secondStart;
                        }else if(xy1>=secondEnd){
                            xy1=secondEnd-1;
                        }
                        rgb1 = getRGB(time==0?x:xy1,time==1?y:xy1, time==0?srcImage:outImage_temp);
                        //-----------------------------------
                        rgb[0]+=(Convolution[i+center]*rgb1[0]);
                        rgb[1]+=(Convolution[i+center]*rgb1[1]);
                        rgb[2]+=(Convolution[i+center]*rgb1[2]);
                        //------------------------------------
                    }
                    /*-----------------*/
                    try{
                        rgb[0]/=modulusOfNormalization;
                        rgb[1]/=modulusOfNormalization;
                        rgb[2]/=modulusOfNormalization;

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    setRGB(time==0?outImage_temp:outImage,x,y,(time==0?srcImage:outImage_temp).getRGB(x,y),rgb);
                }
            }
        }
    }
}
