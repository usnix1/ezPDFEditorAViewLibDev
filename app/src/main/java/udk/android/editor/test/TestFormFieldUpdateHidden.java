package udk.android.editor.test;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import udk.android.editor.env.LibConfiguration;
import udk.android.editor.view.pdf.GlobalConfigurationService;
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
public class TestFormFieldUpdateHidden extends TestBase{
	
	@Override
	public Runnable getAdditionalConfiguration(){
		return new Runnable(){
			@Override
			public void run(){
				
				LibConfiguration.NEW_INPUT_METHOD = true;
				LibConfiguration.USE_FORM = true;
				GlobalConfigurationService.getInstance().setFieldFormattingWithKoreanStyle( true );
			}//method
		};
	}//method

	@Override
	public Workable< View > getOnTest(){
		
		
		return new Workable< View >(){
			@Override
			public void work( View tool ){
				
				String ft = "empty.singleline_02";
				boolean h = getPDFView().getFormFieldFlagHidden( ft );
				Alerter.shortNotice( getPDFView().getContext(), "## DO HIDDEN : " + !h );
				getPDFView().setFormFieldFlagHidden( new String[]{ ft }, !h );
				
				
			}//method
		};
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
				Alerter.shortNotice( context, e.formFieldTitle );
				LogUtil.d( e.formFieldTitle );
			}
			
			@Override
			public void onFormFieldBlured( PDFViewFormEvent e ){
				
			}
		} );
		
		getPDFView().openPDF( "/sdcard/test/form_filled-1.pdf", 0 );
		
	}//method

}//method
