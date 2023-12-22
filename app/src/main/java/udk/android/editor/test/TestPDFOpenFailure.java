package udk.android.editor.test;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import udk.android.reader.env.LibConfiguration;
import udk.android.reader.view.pdf.PDFView;
import udk.android.util.Workable;
import udk.android.widget.Alerter;

/**
 * 
 * @author JEON YONGTAE
 */
public class TestPDFOpenFailure extends TestBase{
	
	@Override
	public Runnable getAdditionalConfiguration(){
		return new Runnable(){
			@Override
			public void run(){
				LibConfiguration.USER_LOG_LEVEL = LibConfiguration.USER_LOG_LEVEL_NONE;
			}//method
		};
	}//method

	@Override
	public Workable< View > getOnTest(){
		return null;
	}//method
	
	private PDFView pdfView;

	@Override
	public void onCreate( Bundle savedInstanceState ){
		
		super.onCreate( savedInstanceState );
		
		final Context context = this;
		pdfView = getPDFView();
		pdfView.setOnPDFOpenFailureListener( new PDFView.OnPDFOpenFailureListener(){
			@Override
			public void onPDFOpenFailure( String errorMessage ){
				Alerter.shortNotice( context, "못열어! %s", errorMessage );
			}//method
		} );
		
		pdfView.openPDF( "/sdcard/nofile.pdf", 0 );
		
	}//method

}//method
