package ImageCanny;

import java.awt.image.BufferedImage;
import java.io.File;

public class NMS {
    public static BufferedImage NMSwithoutPowerWeight(BufferedImage gradImage,double[][] theta,String imageSaveDir){
        int xStart=gradImage.getMinX();
        int yStart=gradImage.getMinY();
        int width =gradImage.getWidth();
        int height=gradImage.getHeight();
        BufferedImage NmsImage=new BufferedImage(width,height,gradImage.getType());

        int rgb1=0,rgb2=0;
        for(int x=xStart;x<width;x++){
            for(int y=yStart;y<height;y++){
                //垂直边缘（theta与边缘正交）
                if(theta[x][y]>=-22.5&&theta[x][y]<22.5||theta[x][y]>=157.5&&theta[x][y]<-157.5){
                    if(y-1>=yStart){
                        rgb1=gradImage.getRGB(x,y-1);
                    }else {
                        rgb1=0;
                    }
                    if(y+1<height){
                        rgb2=gradImage.getRGB(x,y+1);
                    }else {
                        rgb2=0;
                    }

                }//-45边缘
                else if(theta[x][y]>=22.5&&theta[x][y]<67.5||theta[x][y]>=-157.5&&theta[x][y]<-112.5){
                    if(y-1>=yStart&&x-1>=xStart){
                        rgb1=gradImage.getRGB(x-1,y-1);
                    }else {
                        rgb1=0;
                    }
                    if(y+1<height&&x+1<width){
                        rgb2=gradImage.getRGB(x+1,y+1);
                    }else {
                        rgb2=0;
                    }
                }//水平边缘
                else if(theta[x][y]>=67.5&&theta[x][y]<112.5||theta[x][y]>=112.5&&theta[x][y]<-67.5){
                    if(x-1>=xStart){
                        rgb1=gradImage.getRGB(x-1,y);
                    }else {
                        rgb1=0;
                    }
                    if(x+1<width){
                        rgb2=gradImage.getRGB(x+1,y);
                    }else {
                        rgb2=0;
                    }
                }//+45边缘
                else if(theta[x][y]>=112.5&&theta[x][y]<157.5||theta[x][y]>=-67.5&&theta[x][y]<-22.5){
                    if(y-1>=yStart&&x+1<width){
                        rgb1=gradImage.getRGB(x+1,y-1);
                    }else {
                        rgb1=0;
                    }
                    if(y+1<height&&x-1>=xStart){
                        rgb2=gradImage.getRGB(x-1,y+1);
                    }else {
                        rgb2=0;
                    }
                }
                if(gradImage.getRGB(x,y)>=rgb1&&gradImage.getRGB(x,y)>=rgb2){
                    NmsImage.setRGB(x,y,gradImage.getRGB(x,y));
                }else {
                    NmsImage.setRGB(x,y,0);
                }
            }
        }
        try {
            ImageBaseOp.SaveImage(NmsImage, "jpg",new File(imageSaveDir+"NMS.jpg"));
        }catch (Exception e){
            e.printStackTrace();
        }
        return NmsImage;
    }
}
