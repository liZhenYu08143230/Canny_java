import ImageCanny.Canny;

public class Main {
    public static  void main(String[] args){
        String srcPath="H:/Test_JPG/8.jpg";
        String imageSaveDir="C:/Users/lzy01/Desktop/jpg/";
        double sigam =0.8;
        int guiYiMode =0;
        int dimension=1;
        Canny myCanny=new Canny();
        myCanny.CannyPicture(sigam,guiYiMode,dimension,srcPath,imageSaveDir);
    }
}
