package ImageCanny;

import java.awt.image.BufferedImage;

public class ImageBaseOp {


    /**
     *将图像（x，y）的RGB值保存至数组中
     * @param x 获取图片RGB的位置的x坐标
     * @param y 获取图片RGB的位置的y坐标
     * @param image 需要获取的图片
     * @return 返回的RGB数组[0]-r,[1]-g,[2]-b
     */
    public static int[] getRGBInArrary( BufferedImage image,int x, int y) {
        int rgb[]=new int[3];
        int pixel = image.getRGB(x, y);
        rgb[0]=(pixel >> 16) & 0xff;
        rgb[1] = (pixel >> 8) & 0xff;
        rgb[2]= pixel & 0xff;
        return rgb;
    }

    /**
     * 设置srcImage的像素
     * @param srcImage 需要设置RGB值的对象
     * @param x 图片修改位置的x坐标
     * @param y 图片修改位置的y上坐标
     * @param  srcPixel 原像素值
     * @param  rgb 修改的rgb数组
     */
    public static void setRGB(BufferedImage srcImage, int x, int y, int srcPixel, int rgb[]){
        int pixel=0;
        for(int i=0;i<3;i++){
            if(rgb[i]>255||rgb[i]<0){
                rgb[i]=rgb[i]>255?255:0;
            }
        }
        pixel = (rgb[0] << 16) & 0x00ff0000 | (srcPixel & 0xff00ffff);
        pixel = (rgb[1]  << 8) & 0x0000ff00 | (pixel & 0xffff00ff);
        pixel = (rgb[2] ) & 0x000000ff | (pixel & 0xffffff00);
        srcImage.setRGB(x, y, pixel);
    }

    /**
     * 设置srcImage的像素
     * @param srcImage 需要设置RGB值的对象
     * @param x 图片修改位置的x坐标
     * @param y 图片修改位置的y上坐标
     * @param srcPixel 原像素值
     * @param rgbInt 修改的rgb
     */
    public static void setRGB(BufferedImage srcImage, int x, int y, int srcPixel, int rgbInt){
        int pixel;
        pixel = (rgbInt << 16) & 0x00ff0000 | (srcPixel & 0xff00ffff);
        pixel = (rgbInt  << 8) & 0x0000ff00 | (pixel & 0xffff00ff);
        pixel = (rgbInt ) & 0x000000ff | (pixel & 0xffffff00);
        srcImage.setRGB(x, y, pixel);
    }
    /**
     * 使用给定的算子对图像进行一次二维卷积
     * @param srcImage 原图像
     * @param outImage 卷积产生的图像
     * @param Convolution 二维卷积核 int[][]
     * @param modulusOfNormalization 卷积核的归一化系数
     */
    public static void  Convolution(BufferedImage srcImage, BufferedImage outImage, int Convolution[][], int modulusOfNormalization){
        int xStart=srcImage.getMinX();
        int yStart=srcImage.getMinY();
        int width = srcImage.getWidth();
        int height = srcImage.getHeight();
        int border,center;
        border=center=(Convolution[0].length-1)/2;
        int outRGB[]=new int[3];
        for(int x = xStart; x < width ; x ++) {
            for(int y = yStart; y < height; y++) {
                outRGB[0]=outRGB[1]=outRGB[2]=0;//r=g=b=0
                for(int i=-border;i<=border;i++){
                    int x1=x+i;
                    int[] srcRGB;
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
                        srcRGB= getRGBInArrary(srcImage,x1,y1);
                        //-----------------------------------
                        outRGB[0]+=(Convolution[i+center][j+center]*srcRGB[0]);
                        outRGB[1]+=(Convolution[i+center][j+center]*srcRGB[1]);
                        outRGB[2]+=(Convolution[i+center][j+center]*srcRGB[2]);
                        //------------------------------------
                    }
                }
                try{
                    outRGB[0]/=modulusOfNormalization;
                    outRGB[1]/=modulusOfNormalization;
                    outRGB[2]/=modulusOfNormalization;
                }catch (Exception e){
                    e.printStackTrace();
                }
                setRGB(outImage,x,y,srcImage.getRGB(x,y),outRGB);
            }//遍历y
        }//遍历x
    }

    /**
     * 使用给定的算子对图像进行两次一维卷积
     * @param srcImage 原图像
     * @param outImage 卷积产生的图像
     * @param Convolution 一维卷积核 int[]
     * @param modulusOfNormalization 卷积核的归一化系数
     */
    public static void Convolution(BufferedImage srcImage,BufferedImage outImage,int Convolution[],int modulusOfNormalization){
        int xStart=srcImage.getMinX();
        int yStart=srcImage.getMinY();
        int width = srcImage.getWidth();
        int height = srcImage.getHeight();

        int border,center;
        border=center=(Convolution.length-1)/2;

        int outRGB[]=new int[3];
        int timeXY[][]={{xStart,width},{yStart,height}};
        BufferedImage outImage_temp=new BufferedImage(width, height,srcImage.getType());

        for(int time=0;time<2;time++){//两次卷积

            for(int firstXY=timeXY[time][0]; firstXY<timeXY[time][1]; firstXY++){
                int secondStart=timeXY[time==0?1:0][0];
                int secondEnd=timeXY[time==0?1:0][1];

                for(int secondXY=secondStart; secondXY<secondEnd; secondXY++){
                    int x=(time==0?firstXY:secondXY);
                    int y=(time==1?firstXY:secondXY);
                    outRGB[0]=outRGB[1]=outRGB[2]=0;//r=g=b=0

                    for(int i=-border;i<=border;i++){
                        int xy1=secondXY+i;
                        int srcRGB[];

                        if (xy1<secondStart){
                            xy1=secondStart;
                        }else if(xy1>=secondEnd){
                            xy1=secondEnd-1;
                        }
                        srcRGB = getRGBInArrary(time==0?srcImage:outImage_temp,time==0?x:xy1,time==1?y:xy1 );
                        //-----------------------------------
                        outRGB[0]+=(Convolution[i+center]*srcRGB[0]);
                        outRGB[1]+=(Convolution[i+center]*srcRGB[1]);
                        outRGB[2]+=(Convolution[i+center]*srcRGB[2]);
                        //------------------------------------
                    }
                    /*-----------------*/
                    try{
                        outRGB[0]/=modulusOfNormalization;
                        outRGB[1]/=modulusOfNormalization;
                        outRGB[2]/=modulusOfNormalization;

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    setRGB(time==0?outImage_temp:outImage,x,y,(time==0?srcImage:outImage_temp).getRGB(x,y),outRGB);
                }
            }
        }
    }


}
