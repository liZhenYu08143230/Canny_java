package ImageCanny;

import java.awt.image.BufferedImage;
import java.io.File;

import static java.lang.Math.E;
import static java.lang.Math.pow;

public class Gaussian {

    private double sigam;
    private int sizeOfKenel;
    private int normalizationMode;
    private int indexKenelCenter;
    private int gaussianKenel[][];
    private int modulusOfNormalization =0;

    BufferedImage srcImage,outImage;
    String  imageSaveDir;

    public int getModulusOfNormalization(){
        switch (normalizationMode){
            case 0:
                return modulusOfNormalization;
            default:
                return 1;
        }
    }
    public Gaussian(double sigam, int guiYimode, int dimension,BufferedImage srcImage,BufferedImage outImage,String imageSaveDir){
        this.sigam=sigam;
        this.normalizationMode =guiYimode;
        this.srcImage=srcImage;
        this.outImage=outImage;
        this.imageSaveDir=imageSaveDir;
        sizeOfKenel = getSizeOfKenel();
        indexKenelCenter =(sizeOfKenel -1)/2;
        if(dimension==2){
            gaussianKenel =new int[sizeOfKenel][sizeOfKenel];
        }else if(dimension==1){
            gaussianKenel =new int[1][sizeOfKenel];
        }
    }

    //获取最佳的滤波器大小
    private int getSizeOfKenel(){
        int temp= (int) (6*sigam);
        if(temp<=1){
            return 3;
        }
        if(temp%2==1){
            return temp;
        }else{
            return temp+1;
        }
    }

    //滤波器生成
    private void generateGaussianTemplate(){
        double temp[][]=new double[gaussianKenel.length][gaussianKenel[0].length];
        double sigamaSquare2=2*sigam*sigam;
        for(int i = 0; i< gaussianKenel.length; i++){
            int iSubK=i- indexKenelCenter;
            for(int j = 0; j< gaussianKenel[0].length; j++) {
                int jSubK = j- indexKenelCenter;
                double g = (-(pow(iSubK,2)+pow(jSubK,2)) / sigamaSquare2);
                temp[i][j] = pow(E, g);
            }
        }
        //归一化
        Normalization(temp);
        for(int i = 0; i< gaussianKenel.length; i++){
            for(int j = 0; j< gaussianKenel[0].length; j++) {
                gaussianKenel[i][j]=(int) temp[i][j];
                modulusOfNormalization += gaussianKenel[i][j];
            }
        }
    }

    private void Normalization(double[][] temp) {
        double t1;
        switch (normalizationMode){
            case 0:
                t1 =1/temp[0][0];
                break;
            default:
                double sum=0.0;
                for(int i = 0; i< gaussianKenel.length; i++){
                    for(int j = 0; j< gaussianKenel[0].length; j++) {
                        sum+=temp[i][j];
                    }
                }
                t1 =1/sum;
                break;
        }
        for(int i = 0; i< gaussianKenel.length; i++){
            for(int j = 0; j< gaussianKenel[0].length; j++) {
                temp[i][j]*= t1;
            }
        }
    }

    /**
     *
     */
    public void gaussianPicture(){
        generateGaussianTemplate();


        try {
            switch (gaussianKenel.length){
            //使用一维高斯模板两次卷积 O(n*M*N)
            case 1:
                ImageBaseOp.Convolution(srcImage,outImage,gaussianKenel[0],getModulusOfNormalization());
                break;
            //使用二维高斯模板一次卷积 O(n*n*M*N)
            default:
                ImageBaseOp.Convolution(srcImage,outImage,gaussianKenel,getModulusOfNormalization());
                break;
        }
        //保存高斯模糊后的图片
            ImageBaseOp.SaveImage(outImage, "jpg", new File(imageSaveDir+"gaussian.jpg"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
