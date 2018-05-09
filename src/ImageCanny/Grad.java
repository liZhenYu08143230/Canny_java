package ImageCanny;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

import static java.lang.Math.atan2;
import static java.lang.Math.pow;
import static java.lang.StrictMath.sqrt;

public class Grad {

    private static int sobelX[][]={{-1,0,1},{-2,0,2},{-1,0,1}};
    private static int sobelY[][]={{-1,-2,-1},{0,0,0},{1,2,1}};
    /**
     * 使用Sobel算子产生梯度图像所需的Gx，Gy图像
     * @param srcImage 待处理的图片
     * @param outImage 处理完成的图片
     * @param imageSaveDir 结果图片的保存目录
     * @return
     */
    public static void gradPictureSobel(BufferedImage srcImage, BufferedImage outImage,String imageSaveDir){
        int width = srcImage.getWidth();
        int height = srcImage.getHeight();
        //soble 算子的归一化系数
        int modulusOfNormalization=1;
        BufferedImage soble[]=new BufferedImage[2];

        soble[0]=new BufferedImage(width,height,srcImage.getType());
        soble[1]=new BufferedImage(width,height,srcImage.getType());
        double[][] theta=new double[width][height];

        ImageBaseOp.Convolution(srcImage,soble[0],sobelX,modulusOfNormalization);
        ImageBaseOp.Convolution(srcImage,soble[1],sobelY,modulusOfNormalization);

        gradPicture(soble,outImage);

        angleArrary(soble, theta);

        try {
            ImageIO.write(soble[0], "jpg", new File(imageSaveDir+"Gx.jpg"));
            ImageIO.write(soble[1], "jpg", new File(imageSaveDir+"Gy.jpg"));
            ImageIO.write(outImage, "jpg", new File(imageSaveDir+"Grad.jpg"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 利用Gx和Gy计算图像的梯度
     * @param gxGy 保存Gx(x方向的一阶偏微分)Gy(y方向的一阶偏微分)
     * @param outImage 结果图像
     */
    private static void gradPicture(BufferedImage[] gxGy, BufferedImage outImage) {
        int xStart=gxGy[0].getMinX();
        int yStart=gxGy[0].getMinY();
        int width = gxGy[0].getWidth();
        int height = gxGy[0].getHeight();
        int gx[],gy[],rgb[]=new int[3];
        for(int x=xStart;x<width;x++){
            for(int y=yStart;y<height;y++){
                gx= ImageBaseOp.getRGBInArrary(gxGy[0],x,y);
                gy= ImageBaseOp.getRGBInArrary(gxGy[1],x,y);
                for(int i=0;i<3;i++){
                    rgb[i]=(int) sqrt(pow(gx[i],2)+pow(gy[i],2));
                }
                ImageBaseOp.setRGB(outImage,x,y,gxGy[0].getRGB(x,y),rgb);
            }
        }
    }

    /**
     * 梯度角度的计算
     * @param gxGy 原图像的Gx，Gy
     * @param theta 保存结果的数组
     */
    private static void angleArrary(BufferedImage[] gxGy, double theta[][]){
        int xStart=gxGy[0].getMinX();
        int yStart=gxGy[0].getMinY();
        int width = gxGy[0].getWidth();
        int height = gxGy[0].getHeight();
        int gx,gy;
        int temp;
        for(int x=xStart;x<width;x++){
            for(int y=yStart;y<height;y++){
                gx=gxGy[0].getRGB(x,y);
                gy=gxGy[1].getRGB(x,y);
                temp= (int) (atan2(gy,gx)*(180/Math.PI));
                theta[x][y]= temp>=0?temp:temp+360;
            }
        }
    }
}
