import Gaussian.Gaussian;
import Image.ImageClass;

import java.awt.image.BufferedImage;

public class Main {
    public static  void main(String[] args){

        BufferedImage srcImage,grayImage,gsImage1D,soble;
        String path="C:/Users/lzy01/Desktop/jpg/";
        double sigam =0.5;
        int guiYiMode =0;
        //高斯算子生成
        Gaussian gaoSi1= new Gaussian(sigam,guiYiMode ,1);
        gaoSi1.generateGaussianTemplate();


        ImageClass imageClass=new ImageClass();
        //读取原始图片
        srcImage=imageClass.getImage("H:/Test_JPG/lena.jpg");
        //转化为灰度图
        grayImage=imageClass.toGray(srcImage,path+"gray.jpg");

        //高斯模糊 1D
        long startTime1D=System.currentTimeMillis();
        gsImage1D= imageClass.gaussianPicture(grayImage,gaoSi1.getFilter(),gaoSi1.getIntSum(),path );
        long endTime1D=System.currentTimeMillis();
        System.out.println("Running Time gaussian 1d="+(endTime1D-startTime1D)+"ms");

        long startTimeSobel=System.currentTimeMillis();
        soble=imageClass.sobelPicture(gsImage1D,path);
        long endTimeSobel=System.currentTimeMillis();
        System.out.println("Running Time soble="+(endTimeSobel-startTimeSobel)+"ms");

    }
}
