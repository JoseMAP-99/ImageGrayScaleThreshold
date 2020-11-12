package com.fabiyjose.dialogspractise6;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.JPanel;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 *
 * @author José María y Fabián
 */
public class Canvas extends JPanel{
    
    private final int width = 1024;
    private final int height = 768;
    private Mat actualImage = null;
    private Mat previousImage = null;
    private Mat lastImage = null;
    private Mat imgUmbral = null;
    private int actualView = 1; //Imagen actual sin filtros.
    
    public Canvas(){
        this.setPreferredSize(new Dimension(this.width, this.height));
    }
    
    public BufferedImage getActualImage(){
        switch (actualView) {
            case 0:
                return (BufferedImage) HighGui.toBufferedImage(previousImage);
            case 1:
                return (BufferedImage) HighGui.toBufferedImage(actualImage);
            default:
                return (BufferedImage) HighGui.toBufferedImage(imgUmbral);
        }
    }
    
    public boolean nullImage(int viewA){
        switch (viewA) {
            case 0:
                return (previousImage == null);
            case 1:
                return (actualImage == null);
            default:
                return (imgUmbral == null);
        }
    }
    
    @Override
    public void paintComponent(Graphics g){
        if(actualImage == null) return;
        super.paintComponent(g);
        BufferedImage aux = getActualImage();
        g.drawImage(aux, (int)((this.width-aux.getWidth())/2), 
                (int)((this.height-aux.getHeight())/2), null);
    }
    
    public void loadImage(File img){
        if (actualImage != null) previousImage = actualImage.clone();        
        actualImage = Imgcodecs.imread(img.getAbsolutePath());
    }
    
    /**
     * Podrá ver tanto la imagen anterior, la actual sin filtros, y la actual
     * con filtros.0, 1, 2 respectivamente.
     * @param viewA
     */
    public void viewImage(int viewA){
        this.actualView = viewA;
        repaint();
    }
    
    public void saveImage(String path){
        Imgcodecs.imwrite(path, imgUmbral);
    }
    
    public void applyFilter(int umb){
        imgUmbral = umbralizar(actualImage.clone(), umb);
    }
    
    public boolean checkSize(){
        if (actualImage != null){
            if (actualImage.cols() <= this.width && actualImage.rows() <= this.height){
                return true;
            }
        }
        return false;
    }
    
    public void resize(){
        
        double reason = (double) actualImage.cols()/ (double) actualImage.rows();
        int newHeight;
        int newWidth;
                
        if (actualImage.cols() >= actualImage.rows()){            
            newWidth = this.width;
            newHeight = (int) (newWidth/reason);
        } else {
            newHeight = this.height;
            newWidth = (int) (newHeight*reason);
        }
        
        Mat tempM = new Mat();
        Size tempS = new Size(newWidth, newHeight);
        Imgproc.resize(actualImage, tempM, tempS);
        actualImage = tempM.clone();      
        
    }
    
    public void setImage(){
        if (previousImage == null){
            lastImage = null;
        } else{
            lastImage = actualImage.clone();
        }
        imgUmbral = null;
    }
    
    public void restore(){
        actualImage = previousImage.clone();        
        previousImage = lastImage;       
        lastImage = null;
    }
    
    public void reset(){
        actualImage = null;
        previousImage = null;
    }    
    
    private Mat umbralizar(Mat imagen_original, Integer umbral) {
      
        // crear dos imágenes en niveles de gris con el mismo
        // tamaño que la original
        Mat imagenGris = new Mat(imagen_original.rows(),
                                 imagen_original.cols(), CvType.CV_8U);
        
        Mat imagenUmbralizada = new Mat(imagen_original.rows(),
                                        imagen_original.cols(), CvType.CV_8U);
        
        // convierte a niveles de grises la imagen original
        Imgproc.cvtColor(imagen_original, imagenGris, Imgproc.COLOR_BGR2GRAY);
        
        // umbraliza la imagen:
        // - píxeles con nivel de gris > umbral se ponen a 1
        // - píxeles con nivel de gris <= umbra se ponen a 0
        Imgproc.threshold(imagenGris, imagenUmbralizada, umbral, 255,
                          Imgproc.THRESH_BINARY);
        // se devuelve la imagen umbralizada
        
        return imagenUmbralizada;
    }
}
