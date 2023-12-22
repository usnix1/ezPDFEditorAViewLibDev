package udk.android.editor.test;

import android.os.Bundle;
import android.view.View;

import udk.android.reader.env.LibConfiguration;
import udk.android.util.Workable;

/**
 * 
 * @author JEON YONGTAE
 */
public class TestSamsungPTE extends TestBase{

	@Override
	public Runnable getAdditionalConfiguration(){
		return new Runnable(){
			@Override
			public void run(){
				
				LibConfiguration.ZOOM_MIN_DEFAULT_METHOD = LibConfiguration.ZOOM_MIN_DEFAULT_METHOD_AUTOFIT_IMAGEFULL;
				
				LibConfiguration.PDFVIEW_INNER_FACTORY_CLASS = "com.samsung.uieffect.pte.SPTEPDFViewInnerFactory";				
				LibConfiguration.DEBUGDRAW = false;
			}//method
		};
	}//method

	@Override
	public Workable< View > getOnTest(){
		return null;
	}//method
	
	@Override
	public void onCreate( Bundle savedInstanceState ){
		super.onCreate( savedInstanceState );
		
		getPDFView().openPDF( "/sdcard/h.pdf", 1 );
	}//method

}//class
