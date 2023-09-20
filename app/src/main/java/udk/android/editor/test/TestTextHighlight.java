package udk.android.editor.test;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import udk.android.editor.pdf.TextParagraph;
import udk.android.editor.view.pdf.PDFView;
import udk.android.editor.view.pdf.PDFViewAdapter;
import udk.android.editor.view.pdf.PDFViewEvent;
import udk.android.util.Workable;

/**
 * 
 * @author JEON YONGTAE
 */
public class TestTextHighlight extends TestBase{
	
	private PDFView pdfView;
	
	@Override
	public Runnable getAdditionalConfiguration(){
		return new Runnable(){
			@Override
			public void run(){
				
			}//method
		};
	}//method

	@Override
	public Workable< View > getOnTest(){
		
		final Context context = this;
		
		return new Workable< View >(){
			@Override
			public void work( View tool ){
				
				int page = pdfView.getPage();
				if( textParagraphList != null ){
					idx++;
					if( idx >= textParagraphList.size() ){
						idx = -1;
					}//if
					if( idx == -1 ){
						
						pdfView.clearPageTextHighlight();
						
					}else{
						
						TextParagraph next = textParagraphList.get( idx );
						pdfView.highlightPageText( page, next );
						Toast.makeText( context, next.getText(), Toast.LENGTH_SHORT ).show();
					
					}
				}
				

			}//method
		};
	}//method
	
	@Override
	public void onCreate( Bundle savedInstanceState ){
		
		super.onCreate( savedInstanceState );
		
		final Context context = this;
		pdfView = getPDFView();
		pdfView.setPDFViewListener( new PDFViewAdapter(){
			@Override
			public void onOpenCompleted(PDFViewEvent e){
				initPageText( pdfView.getPage() );
			}//method
			@Override
			public void onPageChanged( PDFViewEvent e ){
				initPageText( e.page );
			}//method
		} );
		
		pdfView.openPDF( "/sdcard/w.pdf", 0 );
		
	}//method
	
	private void initPageText( int page ){
		idx = -1;
		textParagraphList = pdfView.getTextParagraphList( page );
	}
	
	private int idx;
	private List< TextParagraph > textParagraphList;

}//method
