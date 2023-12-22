package udk.android.editor.test;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import udk.android.reader.env.LibConfiguration;
import udk.android.reader.view.pdf.PDFView;
import udk.android.util.AbortableProcessCallback;
import udk.android.util.AssignChecker;
import udk.android.util.Workable;
import udk.android.util.vo.menu.MenuCommand;
import udk.android.widget.Alerter;
import udk.android.widget.WidgetFactory;

/**
 * 
 * @author JEON YONGTAE
 */
public class TestAnnotationOffscreen extends TestBase{
	
	private PDFView pdfView;
	
	@Override
	public Runnable getAdditionalConfiguration(){
		return new Runnable(){
			@Override
			public void run(){
				LibConfiguration.USE_QUIZ = true;
				LibConfiguration.ZOOM_MIN_DEFAULT_METHOD = LibConfiguration.ZOOM_MIN_DEFAULT_METHOD_AUTOFIT_IMAGEFULL;
				LibConfiguration.USE_DOUBLE_PAGE_VIEWING = true;
				LibConfiguration.DOUBLE_PAGE_VIEWING = true;
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
				mcs.add( new MenuCommand( "Line 주석 추가", new Runnable(){
					@Override
					public void run(){
						pdfView.uiAnnotV2CreateLine();
					}
				} ) );
				mcs.add( new MenuCommand( "Freehand 주석 추가", new Runnable(){
					@Override
					public void run(){
						pdfView.uiAnnotV2CreateFreehand();
					}
				} ) );
				mcs.add( new MenuCommand( "모든 주석 OFFSCREEN", new Runnable(){
					@Override
					public void run(){
						pdfView.offscreenAllAnnotations();
					}
				} ) );
				mcs.add( new MenuCommand( "모든 주석 ONSCREEN", new Runnable(){
					@Override
					public void run(){
						pdfView.onscreenAllAnnotations();
					}
				} ) );
				mcs.add( new MenuCommand( "주석 작성자 설정", new Runnable(){
					@Override
					public void run(){
						WidgetFactory.uiAbortablePrompt( context, null, null, null, new AbortableProcessCallback<String, Throwable, Throwable>(){
							@Override
							public void onSuccess( String s ){
								LibConfiguration.ANNOTATION_AUTHOR = s;
								Alerter.shortNotice( context, "done : " + s );
							}
							@Override
							public void onFailure( Throwable e ){
							}
							@Override
							public void onAbort( Throwable e ){
							}
						}, "작성자를 입력하세요", null, null );
					}
				} ) );
				mcs.add( new MenuCommand( "Author 필터 설정", new Runnable(){
					@Override
					public void run(){
						WidgetFactory.uiAbortablePrompt( context, null, null, null, new AbortableProcessCallback<String, Throwable, Throwable>(){
							@Override
							public void onSuccess( String s ){
								pdfView.getAnnotationService().setMarkupAnnotationScreenFilterForAuthor( AssignChecker.isAssigned( s ) ? s : null );
								Alerter.shortNotice( context, "done : " + s );
							}
							@Override
							public void onFailure( Throwable e ){
							}
							@Override
							public void onAbort( Throwable e ){
							}
						}, "작성자를 입력하세요", null, null );
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
//		pdfView.openPDF( "/sdcard/outline.pdf", 1 );
		pdfView.openPDF( "/sdcard/test/EBS_귀트영_Sample(160531)_전체.pdf", 1 );
		
	}//method

}//method
