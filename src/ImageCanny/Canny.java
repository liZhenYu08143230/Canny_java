package ImageCanny;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Canny {


    /**
     * 生成Canny图像
     * @param sigam 高斯核的sigam
     * @param guiYiMode 计算高斯核归一化的方式
     * @param dimension 高斯核的维度
     * @param srcImagePath 原图像路径
     * @param imageSaveDir 结果及中间图像保存目录
     */
    public void CannyPicture(double sigam,int guiYiMode,int dimension,String srcImagePath,String imageSaveDir){

        //读取原始图片
        BufferedImage srcImage=getImage(srcImagePath);
        int width = srcImage.getWidth();
        int height = srcImage.getHeight();
        double theta[][];
        BufferedImage grayImage=new BufferedImage(width,height,BufferedImage.TYPE_BYTE_GRAY);
        BufferedImage gsImage1D=new BufferedImage(width,height,BufferedImage.TYPE_BYTE_GRAY);
        BufferedImage gradBYsoble=new BufferedImage(width,height,BufferedImage.TYPE_BYTE_GRAY);
        BufferedImage NMSImage=new BufferedImage(width,height,BufferedImage.TYPE_BYTE_GRAY);


        //转化为灰度图
        long startTimeGray=System.currentTimeMillis();
        Gray.toGray(srcImage,grayImage,imageSaveDir);
        long endTimeGray=System.currentTimeMillis();
        System.out.println("Running Time Gray="+(endTimeGray-startTimeGray)+"ms");


        Gaussian gaussian=new Gaussian(sigam,guiYiMode,dimension,grayImage,gsImage1D,imageSaveDir );

        //高斯模糊 1D
        long startTime1D=System.currentTimeMillis();
        gaussian.gaussianPicture();
        ImageBaseOp.thresholdProcessing(gsImage1D,15);
        long endTime1D=System.currentTimeMillis();
        System.out.println("Running Time Gaussian 1d="+(endTime1D-startTime1D)+"ms");

        //梯度图像
        long startTimeGrad=System.currentTimeMillis();
        theta=Grad.gradPictureSobel(gsImage1D,gradBYsoble,imageSaveDir);
        long endTimeGrad=System.currentTimeMillis();
        System.out.println("Running Time Grad="+(endTimeGrad-startTimeGrad)+"ms");

        //非最大抑制
        long startTimeNMS=System.currentTimeMillis();
        NMSImage= NMS.NMSwithPowerWeight(gradBYsoble,theta,Grad.getGxGy(),imageSaveDir);
        long endTimeNMS=System.currentTimeMillis();
        System.out.println("Running Time NMS="+(endTimeNMS-startTimeNMS)+"ms");

    }
    private BufferedImage getImage(String srcImagePath){
        File file = new File(srcImagePath);
        BufferedImage bi = null;
        try {
            bi = ImageIO.read(file);
        } catch (IOException e) {

            e.printStackTrace();
        }
        return bi;
    }
}
