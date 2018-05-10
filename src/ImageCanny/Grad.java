package ImageCanny;

import java.awt.image.BufferedImage;
import java.io.File;

import static java.lang.Math.*;

public class Grad {

    private static int sobelX[][]={{-1,0,1},{-2,0,2},{-1,0,1}};
    private static int sobelY[][]={{-1,-2,-1},{0,0,0},{1,2,1}};
    private static double theta[][];

    /**
     * 使用Sobel算子产生梯度图像所需的Gx，Gy图像
     * @param srcImage 待处理的图片
     * @param outImage 处理完成的图片
     * @param imageSaveDir 结果图片的保存目录
     * @return
     */
    public static double[][] gradPictureSobel(BufferedImage srcImage, BufferedImage outImage,String imageSaveDir){
        int width = srcImage.getWidth();
        int height = srcImage.getHeight();
        //soble 算子的归一化系数
        int modulusOfNormalization=1;

        BufferedImage sobel[]=new BufferedImage[2];
        sobel[0]=new BufferedImage(width,height,BufferedImage.TYPE_BYTE_GRAY);
        sobel[1]=new BufferedImage(width,height,BufferedImage.TYPE_BYTE_GRAY);

        int gx[][]=new int [width][height];
        int gy[][]=new int [width][height];
        theta=new double[width][height];

        ImageBaseOp.Convolution(srcImage,gx,sobelX,modulusOfNormalization);
        ImageBaseOp.Convolution(srcImage,gy,sobelY,modulusOfNormalization);

        ImageBaseOp.changePicture(sobel[0],gx);
        ImageBaseOp.changePicture(sobel[1],gy);

        ImageBaseOp.QueZhiChuLi(sobel[0],33);
        ImageBaseOp.QueZhiChuLi(sobel[1],33);


        gradPicture(gx,gy,outImage);
        ImageBaseOp.QueZhiChuLi(outImage,0);


        angleArrary(gx,gy);

        try {
            ImageBaseOp.SaveImage(outImage,"jpg",new File(imageSaveDir+"Grad.jpg"));
            ImageBaseOp.SaveImage(sobel[0],"jpg",new File(imageSaveDir+"Gx.jpg"));
            ImageBaseOp.SaveImage(sobel[1],"jpg",new File(imageSaveDir+"Gy.jpg"));
        }catch (Exception e){
            e.printStackTrace();
        }
        return theta;
    }

    /**
     * 利用Gx和Gy计算图像的梯度
     * @param gx 保存的Gx(x方向的一阶偏微分)
     * @param gx 保存的Gy(y方向的一阶偏微分)
     * @param outImage 结果图像
     */
    private static void gradPicture(int[][] gx,int[][] gy, BufferedImage outImage) {
        int width=gx.length;
        int height=gx[0].length;
        int rgb[]=new int[3];
        for(int x=0;x<width;x++){
            for(int y=0;y<height;y++){
//                M(x,y)=sqrt(Gx^2+Gy^2)
                rgb[0]=(int) sqrt(pow(gx[x][y],2)+pow(gy[x][y],2));
//                M(x,y)=|Gx|+|Gy|
//                rgb[0]=abs(gx[x][y])+abs(gy[x][y]);
                rgb[1]=rgb[2]=rgb[0];
                ImageBaseOp.setRGB(outImage,x,y,0xffffffff,rgb);
            }
        }
    }

    /**
     * 梯度角度的计算
     * @param gx 原图像的Gx
     * @param gy 原图像的Gy
     */
    private static void angleArrary(int[][] gx,int [][]gy){
        int width = gx.length;
        int height = gx[0].length;
        double temp;
        for(int x=0;x<width;x++){
            for(int y=0;y<height;y++){
                temp = (atan2(gy[x][y],gx[x][y])*(180/Math.PI));
                //temp = temp>=0?temp:temp+360;
                //temp = temp>=180?temp-180:temp;
                theta[x][y]=temp;
            }
        }
    }
}
