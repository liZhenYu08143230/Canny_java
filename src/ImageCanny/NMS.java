package ImageCanny;

import java.awt.image.BufferedImage;

public class NMS {
    public static BufferedImage NMS(BufferedImage gradImage,double[][] theta,String imageSaveDir){
        int xStart=gradImage.getMinX();
        int yStart=gradImage.getMinY();
        int width =gradImage.getWidth();
        int height=gradImage.getHeight();
        BufferedImage NmsImage=new BufferedImage(width,height,gradImage.getType());
        for(int x=xStart;x<width;x++){
            for(int y=yStart;y<height;y++){

            }
        }
        return NmsImage;
    }
}
