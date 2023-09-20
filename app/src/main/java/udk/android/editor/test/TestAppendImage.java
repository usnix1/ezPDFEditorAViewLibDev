package udk.android.editor.test;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.view.View;

import udk.android.editor.env.LibConfiguration;
import udk.android.editor.view.pdf.PDFView;
import udk.android.util.LogUtil;
import udk.android.util.Workable;

/**
 * 
 * @author JEON YONGTAE
 */
public class TestAppendImage extends TestBase{
	
	private boolean stop;
	private PDFView pdfView;
	
	@Override
	public Runnable getAdditionalConfiguration(){
		return new Runnable(){
			@Override
			public void run(){
				
				LibConfiguration.USE_FORM = true;
				LibConfiguration.USE_EBOOK_MODE = true;
				LibConfiguration.CONTINUOUS_SCROLL_TYPE = LibConfiguration.CONTINUOUS_SCROLL_TYPE_HORIZONTAL;
				LibConfiguration.CONTINUOUS_SCROLL_SEAMLESS = false;
				LibConfiguration.ZOOM_MIN_DEFAULT_METHOD = LibConfiguration.ZOOM_MIN_DEFAULT_METHOD_AUTOFIT_IMAGEFULL;
				
			}//method
		};
	}//method

	@Override
	public Workable< View > getOnTest(){
		
		final Context context = this;
		
		return new Workable< View >(){
			@Override
			public void work( View tool ){
				
				try{
					
					Bitmap bitmap = Bitmap.createBitmap( 768, 1024, Config.ARGB_4444 );
					Canvas canvas = new Canvas( bitmap );
					Paint paint = new Paint();
					paint.setStyle( Paint.Style.STROKE );
					paint.setColor( 0xffff0000 );
					
					Path path = new Path();
					path.moveTo( 100, 100 );
					path.lineTo( 400, 400 );
					
					path.moveTo( 500, 500 );
					path.lineTo( 700, 900 );
					
					canvas.drawPath( path, paint );
					
					pdfView.lockForAppendImage();
					pdfView.trimAndAppendFullPageMonoColorImageWithLock( 1, bitmap, 0x00, 0x00, 0x00 );
					pdfView.unlockForAppendImage();
					
				}catch( Exception ex ){
					LogUtil.e( ex );
				}
			}//method
		};
	}//method
	
	@Override
	public void onCreate( Bundle savedInstanceState ){
		
		super.onCreate( savedInstanceState );
		
		final Context context = this;
		
		
		pdfView = getPDFView();
		pdfView.openPDF( "/sdcard/test/PC_216035023883.pdf", 1 );
		
		
	}//method

}//method
