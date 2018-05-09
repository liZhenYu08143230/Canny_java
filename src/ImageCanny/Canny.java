package ImageCanny;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Canny {
    private int sobelX[][]={{-1,0,1},{-2,0,2},{-1,0,1}};
    private int sobelY[][]={{-1,-2,-1},{0,0,0},{1,2,1}};

    public void CannyPicture(double sigam,int guiYiMode,int dimension,String srcImagePath,String imageSaveDir){
        //读取原始图片
        BufferedImage srcImage=getImage(srcImagePath);
        int width = srcImage.getWidth();
        int height = srcImage.getHeight();
        BufferedImage grayImage=new BufferedImage(width,height,BufferedImage.TYPE_BYTE_GRAY);
        BufferedImage gsImage1D=new BufferedImage(width,height,BufferedImage.TYPE_BYTE_GRAY);
        BufferedImage gradBYsoble=new BufferedImage(width,height,BufferedImage.TYPE_BYTE_GRAY);

        //高斯算子生成
        Gaussian gaussian= new Gaussian(sigam,guiYiMode ,dimension);
        gaussian.generateGaussianTemplate();

        //转化为灰度图
        long startTimeGray=System.currentTimeMillis();
        toGray(srcImage,grayImage,imageSaveDir);
        long endTimeGray=System.currentTimeMillis();
        System.out.println("Running Time Gray="+(endTimeGray-startTimeGray)+"ms");

        //高斯模糊 1D
        long startTime1D=System.currentTimeMillis();
        gaussianPicture(grayImage,gsImage1D,gaussian,imageSaveDir );
        long endTime1D=System.currentTimeMillis();
        System.out.println("Running Time Gaussian 1d="+(endTime1D-startTime1D)+"ms");

        //梯度图像
        long startTimeSobel=System.currentTimeMillis();
        Grad.gradPictureSobel(gsImage1D,gradBYsoble,imageSaveDir);
        long endTimeSobel=System.currentTimeMillis();
        System.out.println("Running Time Grad="+(endTimeSobel-startTimeSobel)+"ms");

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

    /**
     * 彩色转灰度
     * @param srcImage 原图像
     * @param outImage 处理后的图像
     * @param imageSaveDir 保存目录
     * @return
     */
    private void toGray(BufferedImage srcImage,BufferedImage outImage,String  imageSaveDir) {
        int width = srcImage.getWidth();
        int height = srcImage.getHeight();
        int rgb[];
        try {
            for(int y = srcImage.getMinY(); y < height; y++) {
                for(int x = srcImage.getMinX(); x < width ; x ++) {
                    rgb= ImageBaseOp.getRGBInArrary(srcImage,x,y);
                    //加权法的核心,加权法是用图片的亮度作为灰度值的
                    int grayValue = (rgb[0]*299 + rgb[1]*587 + rgb[2]*114 + 500) / 1000;
                    rgb[0]=rgb[1]=rgb[2]=grayValue;
                    ImageBaseOp.setRGB(outImage,x,y,srcImage.getRGB(x, y),rgb);

                }
            }
            ImageIO.write(outImage, "jpg", new File(imageSaveDir+"gray.jpg"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     *
     * @param srcImage 代处理的图片
     * @param outImage 处理后的图片
     * @param gaussian 保存所使用的高斯核的对象
     * @param imageSaveDir 输出图片的保存路径
     * @return
     */
    private void gaussianPicture(BufferedImage srcImage,BufferedImage outImage ,Gaussian gaussian, String imageSaveDir){

        //保存处理图片时用的高斯核
        int [][]gaussianKenel =gaussian.getFilter();
        //高斯核的归一化系数
        int modulusOfNormalization = gaussian.getIntSum();

        switch (gaussianKenel.length){
            //使用一维高斯模板两次卷积 O(n*M*N)
            case 1:
                ImageBaseOp.Convolution(srcImage,outImage,gaussianKenel[0],modulusOfNormalization);
                break;
            //使用二维高斯模板一次卷积 O(n*n*M*N)
            default:
                ImageBaseOp.Convolution(srcImage,outImage,gaussianKenel,modulusOfNormalization);
                break;
        }
        //保存高斯模糊后的图片
        try {
            ImageIO.write(outImage, "jpg", new File(imageSaveDir+"gaussian.jpg"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
