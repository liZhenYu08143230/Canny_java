package ImageCanny;

import java.awt.image.BufferedImage;
import java.io.File;

public class Gray {
    /**
     * 彩色转灰度
     * @param srcImage 原图像
     * @param outImage 处理后的图像
     * @param imageSaveDir 保存目录
     * @return
     */
    public static void toGray(BufferedImage srcImage, BufferedImage outImage, String  imageSaveDir) {
        int width = srcImage.getWidth();
        int height = srcImage.getHeight();
        int rgb[];
        try {
            for(int y = srcImage.getMinY(); y < height; y++) {
                for(int x = srcImage.getMinX(); x < width ; x ++) {
                    rgb= ImageBaseOp.getRgbArrary(srcImage,x,y);
                    //加权法的核心,加权法是用图片的亮度作为灰度值的
                    int grayValue = (rgb[0]*299 + rgb[1]*587 + rgb[2]*114 + 500) / 1000;
                    rgb[0]=rgb[1]=rgb[2]=grayValue;
                    ImageBaseOp.setRGB(outImage,x,y, rgb, srcImage.getRGB(x, y));
                }
            }
            ImageBaseOp.SaveImage(outImage,"jpg",new File(imageSaveDir+"gray.jpg"));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
