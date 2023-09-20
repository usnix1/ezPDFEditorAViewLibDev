package udk.android.editor.test;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import udk.android.editor.env.LibConfiguration;
import udk.android.editor.view.pdf.GlobalConfigurationService;
import udk.android.editor.view.pdf.PDFView;
import udk.android.editor.view.pdf.PDFViewFormEvent;
import udk.android.editor.view.pdf.PDFViewFormListener;
import udk.android.util.Workable;

/**
 * 
 * @author JEON YONGTAE
 */
public class TestFormFieldZooming extends TestBase{
	
	@Override
	public Runnable getAdditionalConfiguration(){
		return new Runnable(){
			@Override
			public void run(){
				
				LibConfiguration.USE_FORM = true;
				GlobalConfigurationService.getInstance().setFieldFormattingWithKoreanStyle( true );
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
				pdfView.updateDirectlyToFormField( 8.0f, e.formFieldTitle );
			}
			@Override
			public void onFormFieldBlured( PDFViewFormEvent e ){
				
			}
		} );
		
		getPDFView().openPDF( "/sdcard/form_filled-1.pdf", 0 );
		
	}//method

}//method
