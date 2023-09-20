package udk.android.editor.test;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import udk.android.editor.env.LibConfiguration;
import udk.android.editor.view.pdf.PDFView;
import udk.android.util.Workable;

/**
 * 
 * @author JEON YONGTAE
 */
public class Test extends TestBase{
	
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

			}//method
		};
	}//method
	
	@Override
	public void onCreate( Bundle savedInstanceState ){
		
		super.onCreate( savedInstanceState );
		
		final Context context = this;
		
		
		pdfView = getPDFView();
//		pdfView.openPDF( "/sdcard/test/PFA_Bk2.pdf", 4, null );
		pdfView.openPDF( "/sdcard/test/movietest3.pdf", 1, null );
		
	}//method

}//method
