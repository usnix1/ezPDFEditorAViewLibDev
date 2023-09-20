package udk.android.editor.test;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import udk.android.editor.env.LibConfiguration;
import udk.android.editor.view.BannerView;
import udk.android.editor.view.pdf.GlobalConfigurationService;
import udk.android.editor.view.pdf.PDFView;
import udk.android.editor.view.pdf.PDFViewEvent;
import udk.android.editor.view.pdf.PDFViewListenerEx;
import udk.android.util.LogUtil;
import udk.android.util.Workable;
import udk.android.util.vo.menu.MenuCommand;
import udk.android.widget.Alerter;
import udk.android.widget.WidgetFactory;

/**
 * 
 * @author JEON YONGTAE
 */
public class TestQuizWebtoon extends TestBase implements PDFViewListenerEx{
	
	private PDFView pdfView;
	
	@Override
	public Runnable getAdditionalConfiguration(){
		return new Runnable(){
			@Override
			public void run(){
			
				LogUtil.DEBUG = true;
				LibConfiguration.DEBUGDRAW = true;
				LibConfiguration.USER_LOG_LEVEL = LibConfiguration.USER_LOG_LEVEL_NONE;
				LibConfiguration.USE_QUIZ = true;
				LibConfiguration.USE_DOUBLE_PAGE_VIEWING = false;
				LibConfiguration.ENABLE_USERACTION_DOUBLETAP = false;
				LibConfiguration.USE_FORM = true;
				
				
				LibConfiguration.ZOOM_MIN_DEFAULT_METHOD = LibConfiguration.ZOOM_MIN_DEFAULT_METHOD_WIDTHFIT;
				LibConfiguration.CONTINUOUS_SCROLL_PAGE_TERM = 0;
				LibConfiguration.CONTINUOUS_SCROLL_PAGE_TERM_AUTO = false;
				LibConfiguration.CONTINUOUS_SCROLL_SEAMLESS = true;
				LibConfiguration.USE_EBOOK_MODE = false;
				LibConfiguration.DOUBLE_PAGE_VIEWING = false;
				
				LibConfiguration.USE_TOOLBAR = false;
				LibConfiguration.CACHING_PARAM_ENCRYPT_ALWAYS = false;
				LibConfiguration.OPEN_METHOD_NO_CACHING = false;
				
				LibConfiguration.LQ_PRERENDER = true;
				LibConfiguration.LQ_PRERENDER_IN_FASTOPEN = true;

				LibConfiguration.AUDIO_PLAY_WITH_TEXT_HIGHLIGHT = true;
				LibConfiguration.USE_BANNER_VIEW = true;
				LibConfiguration.USE_ANNOTATION_PAGE_ACTION_BY_OBJECT = true;
				GlobalConfigurationService.getInstance().setDefaultBackgroundColor24(Color.WHITE);
				
				
			}//method
		};
	}//method

	
	private boolean toPrev;
	
	@Override
	public Workable< View > getOnTest(){
		final Context context = this;
		return new Workable< View >(){
			@Override
			public void work( View tool ){
				
//				if( 1 == 1 ){
//					try{
//						long s = System.currentTimeMillis();
//						pdfView.flatten();
////						LogUtil.d( "DONE : " + ( System.currentTimeMillis() - s ) );
//						Alerter.shortNotice( context, "DONE : " + ( System.currentTimeMillis() - s ) );
//					}catch( Exception ex ){
//						LogUtil.e( ex );
//					}
//					return;
//				}
				
				List< MenuCommand > mcs = new ArrayList< MenuCommand >();
				mcs.add( new MenuCommand( "채점하기", new Runnable(){
					@Override
					public void run(){
						
						pdfView.getQuizService().markGrade();
						
					}//method
				} ) );
				mcs.add( new MenuCommand( "채점지우기", new Runnable(){
					@Override
					public void run(){
						
						pdfView.getQuizService().unmarkGrade();
						
					}//method
				} ) );
				mcs.add( new MenuCommand( "이 페이지에 문제풀이 있는지 확인", new Runnable(){
					@Override
					public void run(){
						
						boolean hasQuiz = pdfView.hasQuiz( pdfView.getPage() );
						Alerter.shortNotice( context, "페이지 " + pdfView.getPage() + " : " + hasQuiz );
						
					}//method
				} ) );
				mcs.add( new MenuCommand( "이 페이지에 클리커 있는지 확인", new Runnable(){
					@Override
					public void run(){
						
						boolean hasClicker = pdfView.hasQuizClicker( pdfView.getPage() );
						Alerter.shortNotice( context, "페이지 " + pdfView.getPage() + " : " + hasClicker );
						
					}//method
				} ) );
				mcs.add( new MenuCommand( "이 페이지 클리커 리셋", new Runnable(){
					@Override
					public void run(){
						
						pdfView.resetPageQuizClicker( pdfView.getPage() );
						
					}//method
				} ) );
				mcs.add( new MenuCommand( "이 페이지 날리지탭 리셋", new Runnable(){
					@Override
					public void run(){
						
						pdfView.resetPageQuizKnowledgeTap( pdfView.getPage() );
						
					}//method
				} ) );				
				mcs.add( new MenuCommand( "단면/양면 전환", new Runnable(){
					@Override
					public void run(){
						
						pdfView.setDoublePageViewing( !pdfView.isDoublePageViewing() );
						
					}
				} ) );
				mcs.add( new MenuCommand( "이전페이지", new Runnable(){
					@Override
					public void run(){
						
						pdfView.prevPage();
						
					}
				} ) );
				mcs.add( new MenuCommand( "다음페이지", new Runnable(){
					@Override
					public void run(){
						
						pdfView.nextPage();
						
					}
				} ) );
				mcs.add( new MenuCommand( "멀티미디어 재생", new Runnable(){
					@Override
					public void run(){
						pdfView.uiPlayAllMultimedia( context, null );
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
		pdfView.setBannerView(new BannerView(getBanner(), new BannerView.BannerGetHeightCallback() {
		@Override
		public int getBannerViewHeight(int width) {
			Log.e("", "getBannerViewHeight : " + width) ;

			float pow = width / 500;
			return (int)(400 * pow);
		}
	}));
		pdfView.setPDFViewListenerEx(this);
		
//		pdfView.openPDF( "http://webviewer.unidocs.co.kr/contube/sample/manga_Sample.o.pdf", 0 );
//		pdfView.openPDF( "http://webviewer.unidocs.co.kr/contube/sample/manga_Sample.o2.pdf", 0 );
		pdfView.openPDF( "http://www.cu-book.com/manga_Sample.o3.pdf", 1 );
//		pdfView.openPDF( "http://demo.contube.co.kr/sample/comicoplus.l.pdf", 1 );
//		pdfView.openPDF( "/sdcard/test/[interactive]코미코 만화_Sample.linearized.pdf", 0 );
		
	}//method

	private View getBanner() {
		LinearLayout layout = new LinearLayout(this);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(500, 400);
		layout.setLayoutParams(params);
		layout.setBackgroundColor(Color.GREEN);
		layout.setGravity(Gravity.CENTER);

		View view = new View(this);
		view.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick (View arg0){
				Log.e("", "onClick");
			}
		}

		);

		view.setBackgroundColor(Color.GRAY);
		params=new LinearLayout.LayoutParams(300,200);
		layout.addView(view,params);

		return layout;
	}

	@Override
	public void onPageChangeTried(PDFViewEvent e) {
//		Log.e("", "onPageChangeTried : " + e.page);
	}
}//method
