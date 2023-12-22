package udk.android.editor.test;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import udk.android.reader.pdf.annotation.FreeTextAnnotation;
import udk.android.reader.view.pdf.PDFView;
import udk.android.util.RandomUtil;
import udk.android.util.Workable;
import udk.android.util.vo.menu.MenuCommand;
import udk.android.widget.Alerter;
import udk.android.widget.WidgetFactory;

/**
 * 
 * @author JEON YONGTAE
 */
public class TestAnnotationAddTextBox extends TestBase{
	
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
				
				List< MenuCommand > mcs = new ArrayList< MenuCommand >();
				mcs.add( new MenuCommand( "TextBox 주석 추가", new Runnable(){
					@Override
					public void run(){
						pdfView.prepareAddAnnotationTextBoxStart( new Workable< FreeTextAnnotation >(){
							@Override
							public void work( FreeTextAnnotation a ){
								
								//UI를 통한 내용 입력
								String input = "흠냐";
								
								if( RandomUtil.getRandomInFromTo( 1, 2 ) == 1 ){
									
									//사용자 최종 컨펌
									a.setContents( input );
									pdfView.addAnnotationTextBoxEndConfirm( a ); 
									Alerter.shortNotice( context, "주석생성완료" );
									
								}else{
									
									//사용자 취소
									pdfView.addAnnotationTextBoxEndCancel( a );
									Alerter.shortNotice( context, "사용자 취소" );
								
								}
							}
						} );
					}
				} ) );
				
				WidgetFactory.uiPopupMenu( tool, mcs );
				
			}//method
		};
	}//method
	
	@Override
	public void onCreate( Bundle savedInstanceState ){
		
		super.onCreate( savedInstanceState );
		
		final Context context = this;
		
		
		pdfView = getPDFView();
		pdfView.openPDF( "/sdcard/outline.pdf", 1 );

		
	}//method

}//method
