package ImageCanny;

import static java.lang.Math.*;

public class Gaussian {

    private int dimension;
    private double sigam;
    private int sizeFilter;
    private int guiYiMode;
    private double guiYi;
    private int k, kSquare2;
    private int filter[][], intSum =0;

    private int getIntSum(){
        return intSum;
    }
    public Gaussian(double sigam, int guiYimode, int dimension){
        this.sigam=sigam;
        this.guiYiMode =guiYimode;
        this.dimension=dimension;
        sizeFilter =getSizeFilter();
        k=(sizeFilter -1)/2;
        if(dimension==2){
            filter=new int[sizeFilter][sizeFilter];
        }else if(dimension==1){
            filter=new int[1][sizeFilter];
        }
        kSquare2 =2*k*k;
    }

    //获取最佳的滤波器大小
    private int getSizeFilter(){
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
    public void generateGaussianTemplate(){
        double temp[][]=new double[filter.length][filter[0].length];
        double sigamaSquare2=2*sigam*sigam;
        for(int i = 0; i< filter.length; i++){
            int iSubK=i-k;
            for(int j = 0; j< filter[0].length; j++) {
                int jSubK = j -k;
                double g = (-(pow(iSubK,2)+pow(jSubK,2)) / sigamaSquare2);
                temp[i][j] = pow(E, g);
            }
        }
        //归一化
        GuiYi(temp);
        for(int i = 0; i< filter.length; i++){
            for(int j = 0; j< filter[0].length; j++) {
                filter[i][j]=(int) temp[i][j];
                intSum +=filter[i][j];
            }
        }
    }

    private void GuiYi(double[][] temp) {
        switch (guiYiMode){
            case 0:
                guiYi=1/temp[0][0];
                break;
            default:
                double sum=0.0;
                for(int i = 0; i< filter.length;i++){
                    for(int j = 0; j<filter[0].length; j++) {
                        sum+=temp[i][j];
                    }
                }
                guiYi=1/sum;
                break;
        }
        for(int i = 0; i< filter.length; i++){
            for(int j = 0; j< filter[0].length; j++) {
                temp[i][j]*=guiYi;
            }
        }
    }

    public int[][] getFilter(){
        return  filter;
    }
}
