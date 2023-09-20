package udk.android.editor.test;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import udk.android.editor.env.LibConfiguration;
import udk.android.editor.view.pdf.PDFView;
import udk.android.editor.view.pdf.PDFViewFormEvent;
import udk.android.editor.view.pdf.PDFViewFormListener;
import udk.android.util.LogUtil;
import udk.android.util.Workable;
import udk.android.widget.Alerter;

/**
 * 
 * @author JEON YONGTAE
 */
public class TestFormFieldSetPushButtonFieldImage extends TestBase{
	
	@Override
	public Runnable getAdditionalConfiguration(){
		return new Runnable(){
			@Override
			public void run(){
				
				LibConfiguration.USE_FORM = true;
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
		
		final Context context = this;
		
		final PDFView pdfView = getPDFView();
		pdfView.setPDFViewFormListener( new PDFViewFormListener(){
			@Override
			public void onFormFieldValueUpdated( PDFViewFormEvent e ){
			}
			
			@Override
			public void onFormFieldTapped( PDFViewFormEvent e ){
				boolean imageSetted = pdfView.isButtonFieldHasImageData( e.formFieldTitle );
				Alerter.shortNotice( context, "IMAGE SETTED : " + imageSetted );
				
				if( "PushButtonField".equals( e.formFieldType ) ){
					try{
						pdfView.setBtnFieldImage( e.formFieldTitle, "/sdcard/2.jpg" );
					}catch( Exception ex ){
						LogUtil.e( ex );
					}
				}
				
				imageSetted = pdfView.isButtonFieldHasImageData( e.formFieldTitle );
				Alerter.shortNotice( context, "IMAGE SETTED : " + imageSetted );
			}
			
			@Override
			public void onFormFieldBlured( PDFViewFormEvent e ){
			}
		} );
		
		getPDFView().openPDF( "/sdcard/form_filled-2.pdf", 0 );
		
	}//method

}//method
