package hslu.bda.medimemory.detection;

import android.app.Activity;
import android.app.Application;
import android.graphics.Bitmap;

import android.media.ExifInterface;
import android.view.Display;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import hslu.bda.medimemory.entity.PillCoords;

/**
 * Created by Andy on 24.04.2016.
 */
public class PillDetection {

    private String selectedImagePath = "";
    private Mat sampledImage;
    private Mat originalImage;

    public PillDetection(String picturePath, Activity activity){
        originalImage = Imgcodecs.imread(picturePath);
        loadImage(activity);
    }

    public PillDetection(Bitmap bitmap, Activity activity){
        Utils.bitmapToMat(bitmap, originalImage);
        loadImage(activity);
    }

    private void loadImage(Activity activity){
        Mat rgbImage=new Mat();

        Imgproc.cvtColor(originalImage, rgbImage, Imgproc.COLOR_BGR2RGB);

        Display display = activity.getWindowManager().getDefaultDisplay();
        //This is "android graphics Point" class
        android.graphics.Point size = new android.graphics.Point();
        display.getSize(size);

        int width = (int) size.x;
        int height = (int) size.y;
        sampledImage=new Mat();

        double downSampleRatio= calculateSubSampleSize(rgbImage, width, height);

        Imgproc.resize(rgbImage, sampledImage, new Size(), downSampleRatio, downSampleRatio, Imgproc.INTER_AREA);

        try {
            ExifInterface exif = new ExifInterface(selectedImagePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

            switch (orientation)
            {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    //get the mirrored image
                    sampledImage=sampledImage.t();
                    //flip on the y-axis
                    Core.flip(sampledImage, sampledImage, 1);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    //get up side down image
                    sampledImage=sampledImage.t();
                    //Flip on the x-axis
                    Core.flip(sampledImage, sampledImage, 0);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private double calculateSubSampleSize(Mat srcImage, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = srcImage.height();
        final int width = srcImage.width();
        double inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of requested height and width to the raw
            //height and width
            final double heightRatio = (double) reqHeight / (double) height;
            final double widthRatio = (double) reqWidth / (double) width;

            // Choose the smallest ratio as inSampleSize value, this will
            //guarantee final image with both dimensions larger than or
            //equal to the requested height and width.
            inSampleSize = heightRatio<widthRatio ? heightRatio :widthRatio;
        }
        return inSampleSize;
    }

    public Collection<PillCoords> getAllPillPoints(int mediid) throws Throwable{
        Collection<PillCoords> allPillPoints = new ArrayList<PillCoords>();
        try {
            Mat orgImage = sampledImage.clone();
            Mat rgb_image = new Mat();
            Mat blur_image = new Mat();

            Scalar lowerWhite = new Scalar(160,160,160);
            Scalar biggerWhite = new Scalar(255,255,255);

            Core.inRange(orgImage, lowerWhite, biggerWhite, rgb_image);
            Imgproc.GaussianBlur(rgb_image, blur_image, new Size(5, 5), 0);
            ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
            Mat hierarchy = new Mat();

            Imgproc.findContours(blur_image, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
            for(int contourIdx = 0; contourIdx < contours.size(); contourIdx++){

                if(Imgproc.contourArea(contours.get(contourIdx))>1500){
                    //System.out.println("Contour ARea: "+Imgproc.contourArea(contours.get(contourIdx)));
                    Rect rect = Imgproc.boundingRect(contours.get(contourIdx));
                    //System.out.println("rect.height: "+rect.height);
                    if(rect.height>10){
                        Moments moments = Imgproc.moments(contours.get(contourIdx));
                        Point point = new Point();
                        point.x = moments.get_m10() / moments.get_m00();
                        point.y = moments.get_m01() / moments.get_m00();
                        PillCoords pillCoords = new PillCoords(0, mediid, point,rect.width, rect.height);
                        allPillPoints.add(pillCoords);
                    }

                }
            }

        }catch (Exception e){
            throw new Throwable(e);
        }


        return allPillPoints;
    }

}
