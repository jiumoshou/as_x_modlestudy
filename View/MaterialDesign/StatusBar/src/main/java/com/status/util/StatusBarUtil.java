package com.status.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.status.R;
import com.yline.utils.UIScreenUtil;

public class StatusBarUtil
{
	public static final int DEFAULT_STATUS_BAR_ALPHA = 112; // 半透明

	private static final int TAG_KEY_HAVE_SET_OFFSET = -123;

	private static final int FAKE_STATUS_BAR_VIEW_ID = R.id.statusbarutil_fake_status_bar_view;

	private static final int FAKE_TRANSLUCENT_VIEW_ID = R.id.statusbarutil_translucent_view;

	/**
	 * 设置状态栏全透明
	 *
	 * @param activity 需要设置的activity
	 */
	public static void setTransparent(Activity activity)
	{
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
		{
			return;
		}
		transparentStatusBar(activity);
		setRootView(activity);
	}

	/**
	 * 使状态栏半透明
	 * <p>
	 * 适用于图片作为背景的界面,此时需要图片填充到状态栏
	 *
	 * @param activity       需要设置的activity
	 * @param statusBarAlpha 状态栏透明度
	 */
	public static void setTranslucent(Activity activity, int statusBarAlpha)
	{
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
		{
			return;
		}
		setTransparent(activity);
		addTranslucentView(activity, statusBarAlpha);
	}

	/**
	 * 为 fragment 头部是 ImageView 的设置状态栏透明
	 *
	 * @param activity       fragment 对应的 activity
	 * @param needOffsetView 需要向下偏移的 View
	 */
	public static void setTranslucentForImageViewInFragment(Activity activity, View needOffsetView)
	{
		setTranslucentForImageViewInFragment(activity, DEFAULT_STATUS_BAR_ALPHA, needOffsetView);
	}

	/**
	 * 为 fragment 头部是 ImageView 的设置状态栏透明
	 *
	 * @param activity       fragment 对应的 activity
	 * @param statusBarAlpha 状态栏透明度
	 * @param needOffsetView 需要向下偏移的 View
	 */
	public static void setTranslucentForImageViewInFragment(Activity activity, int statusBarAlpha, View needOffsetView)
	{
		setTranslucentForImageView(activity, statusBarAlpha, needOffsetView);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
		{
			clearPreviousSetting(activity);
		}
	}

	/**
	 * 为头部是 ImageView 的界面设置状态栏透明(使用默认透明度)
	 *
	 * @param activity       需要设置的activity
	 * @param needOffsetView 需要向下偏移的 View
	 */
	public static void setTranslucentForImageView(Activity activity, View needOffsetView)
	{
		setTranslucentForImageView(activity, DEFAULT_STATUS_BAR_ALPHA, needOffsetView);
	}

	/**
	 * 为头部是 ImageView 的界面设置状态栏透明
	 *
	 * @param activity       需要设置的activity
	 * @param statusBarAlpha 状态栏透明度
	 * @param needOffsetView 需要向下偏移的 View
	 */
	public static void setTranslucentForImageView(Activity activity, int statusBarAlpha, View needOffsetView)
	{
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
		{
			return;
		}
		setTransparentForWindow(activity);
		addTranslucentView(activity, statusBarAlpha);
		if (needOffsetView != null)
		{
			Object haveSetOffset = needOffsetView.getTag(TAG_KEY_HAVE_SET_OFFSET);
			if (haveSetOffset != null && (Boolean) haveSetOffset)
			{
				return;
			}
			ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) needOffsetView.getLayoutParams();
			layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin + UIScreenUtil.getStatusHeight(activity), layoutParams.rightMargin, layoutParams.bottomMargin);
			needOffsetView.setTag(TAG_KEY_HAVE_SET_OFFSET, true);
		}
	}

	/**
	 * 为 DrawerLayout 布局设置状态栏透明
	 *
	 * @param activity     需要设置的activity
	 * @param drawerLayout DrawerLayout
	 */
	public static void setTranslucentForDrawerLayout(Activity activity, DrawerLayout drawerLayout, int statusBarAlpha)
	{
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
		{
			return;
		}
		setTransparentForDrawerLayout(activity, drawerLayout);
		addTranslucentView(activity, statusBarAlpha);
	}

	/**
	 * 为 DrawerLayout 布局设置状态栏透明
	 *
	 * @param activity     需要设置的activity
	 * @param drawerLayout DrawerLayout
	 */
	public static void setTransparentForDrawerLayout(Activity activity, DrawerLayout drawerLayout)
	{
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
		{
			return;
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
		{
			activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
		}
		else
		{
			activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		}

		ViewGroup contentLayout = (ViewGroup) drawerLayout.getChildAt(0);
		// 内容布局不是 LinearLayout 时,设置padding top
		if (!(contentLayout instanceof LinearLayout) && contentLayout.getChildAt(1) != null)
		{
			contentLayout.getChildAt(1).setPadding(0, UIScreenUtil.getStatusHeight(activity), 0, 0);
		}

		// 设置属性
		setDrawerLayoutProperty(drawerLayout, contentLayout);
	}

	/**
	 * 设置状态栏颜色
	 *
	 * @param activity 需要设置的 activity
	 * @param color    状态栏颜色值
	 */
	public static void setColor(Activity activity, @ColorInt int color)
	{
		setColor(activity, color, DEFAULT_STATUS_BAR_ALPHA);
	}

	/**
	 * 设置状态栏颜色
	 *
	 * @param activity       需要设置的activity
	 * @param color          状态栏颜色值
	 * @param statusBarAlpha 状态栏透明度
	 */

	public static void setColor(Activity activity, @ColorInt int color, int statusBarAlpha)
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
		{
			activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			activity.getWindow().setStatusBarColor(calculateStatusColor(color, statusBarAlpha));
		}
		else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
		{
			activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
			View fakeStatusBarView = decorView.findViewById(FAKE_STATUS_BAR_VIEW_ID);
			if (fakeStatusBarView != null)
			{
				if (fakeStatusBarView.getVisibility() == View.GONE)
				{
					fakeStatusBarView.setVisibility(View.VISIBLE);
				}
				fakeStatusBarView.setBackgroundColor(calculateStatusColor(color, statusBarAlpha));
			}
			else
			{
				decorView.addView(createStatusBarView(activity, color, statusBarAlpha));
			}
			setRootView(activity);
		}
	}

	/**
	 * 为滑动返回界面设置状态栏颜色
	 *
	 * @param activity       需要设置的activity
	 * @param color          状态栏颜色值
	 * @param statusBarAlpha 状态栏透明度
	 */
	public static void setColorForSwipeBack(Activity activity, @ColorInt int color, int statusBarAlpha)
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
		{

			ViewGroup contentView = ((ViewGroup) activity.findViewById(android.R.id.content));
			View rootView = contentView.getChildAt(0);
			int statusBarHeight = UIScreenUtil.getStatusHeight(activity);
			if (rootView != null && rootView instanceof CoordinatorLayout)
			{
				final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) rootView;
				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
				{
					coordinatorLayout.setFitsSystemWindows(false);
					contentView.setBackgroundColor(calculateStatusColor(color, statusBarAlpha));
					boolean isNeedRequestLayout = contentView.getPaddingTop() < statusBarHeight;
					if (isNeedRequestLayout)
					{
						contentView.setPadding(0, statusBarHeight, 0, 0);
						coordinatorLayout.post(new Runnable()
						{
							@Override
							public void run()
							{
								coordinatorLayout.requestLayout();
							}
						});
					}
				}
				else
				{
					coordinatorLayout.setStatusBarBackgroundColor(calculateStatusColor(color, statusBarAlpha));
				}
			}
			else
			{
				contentView.setPadding(0, statusBarHeight, 0, 0);
				contentView.setBackgroundColor(calculateStatusColor(color, statusBarAlpha));
			}
			setTransparentForWindow(activity);
		}
	}

	/**
	 * 为DrawerLayout 布局设置状态栏颜色,纯色
	 *
	 * @param activity     需要设置的activity
	 * @param drawerLayout DrawerLayout
	 * @param color        状态栏颜色值
	 */
	public static void setColorNoTranslucentForDrawerLayout(Activity activity, DrawerLayout drawerLayout, @ColorInt int color)
	{
		setColorForDrawerLayout(activity, drawerLayout, color, 0);
	}

	/**
	 * 为DrawerLayout 布局设置状态栏变色
	 *
	 * @param activity       需要设置的activity
	 * @param drawerLayout   DrawerLayout
	 * @param color          状态栏颜色值
	 * @param statusBarAlpha 状态栏透明度
	 */
	public static void setColorForDrawerLayout(Activity activity, DrawerLayout drawerLayout, @ColorInt int color, int statusBarAlpha)
	{
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
		{
			return;
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
		{
			activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
		}
		else
		{
			activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		}
		// 生成一个状态栏大小的矩形
		// 添加 statusBarView 到布局中
		ViewGroup contentLayout = (ViewGroup) drawerLayout.getChildAt(0);
		View fakeStatusBarView = contentLayout.findViewById(FAKE_STATUS_BAR_VIEW_ID);
		if (fakeStatusBarView != null)
		{
			if (fakeStatusBarView.getVisibility() == View.GONE)
			{
				fakeStatusBarView.setVisibility(View.VISIBLE);
			}
			fakeStatusBarView.setBackgroundColor(color);
		}
		else
		{
			contentLayout.addView(createStatusBarView(activity, color), 0);
		}
		// 内容布局不是 LinearLayout 时,设置padding top
		if (!(contentLayout instanceof LinearLayout) && contentLayout.getChildAt(1) != null)
		{
			contentLayout.getChildAt(1)
					.setPadding(contentLayout.getPaddingLeft(), UIScreenUtil.getStatusHeight(activity) + contentLayout.getPaddingTop(),
							contentLayout.getPaddingRight(), contentLayout.getPaddingBottom());
		}
		// 设置属性
		setDrawerLayoutProperty(drawerLayout, contentLayout);
		addTranslucentView(activity, statusBarAlpha);
	}

	///////////////////////////////////////////////////////////////////////////////////

	@TargetApi(Build.VERSION_CODES.KITKAT)
	private static void clearPreviousSetting(Activity activity)
	{
		ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
		View fakeStatusBarView = decorView.findViewById(FAKE_STATUS_BAR_VIEW_ID);
		if (fakeStatusBarView != null)
		{
			decorView.removeView(fakeStatusBarView);
			ViewGroup rootView = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
			rootView.setPadding(0, 0, 0, 0);
		}
	}

	/**
	 * 生成一个和状态栏大小相同的彩色矩形条
	 *
	 * @param activity 需要设置的 activity
	 * @param color    状态栏颜色值
	 * @return 状态栏矩形条
	 */
	private static View createStatusBarView(Activity activity, @ColorInt int color)
	{
		return createStatusBarView(activity, color, 0);
	}

	/**
	 * 使状态栏透明
	 */
	@TargetApi(Build.VERSION_CODES.KITKAT)
	private static void transparentStatusBar(Activity activity)
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
		{
			activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
			activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
		}
		else
		{
			activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		}
	}

	/**
	 * 生成一个和状态栏大小相同的半透明矩形条
	 *
	 * @param activity 需要设置的activity
	 * @param color    状态栏颜色值
	 * @param alpha    透明值
	 * @return 状态栏矩形条
	 */
	private static View createStatusBarView(Activity activity, @ColorInt int color, int alpha)
	{
		// 绘制一个和状态栏一样高的矩形
		View statusBarView = new View(activity);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UIScreenUtil.getStatusHeight(activity));
		statusBarView.setLayoutParams(params);
		statusBarView.setBackgroundColor(calculateStatusColor(color, alpha));
		statusBarView.setId(FAKE_STATUS_BAR_VIEW_ID);
		return statusBarView;
	}

	/**
	 * 设置根布局参数
	 */
	private static void setRootView(Activity activity)
	{
		ViewGroup parent = (ViewGroup) activity.findViewById(android.R.id.content);
		for (int i = 0, count = parent.getChildCount(); i < count; i++)
		{
			View childView = parent.getChildAt(i);
			if (childView instanceof ViewGroup)
			{
				childView.setFitsSystemWindows(true);
				((ViewGroup) childView).setClipToPadding(true);
			}
		}
	}

	/**
	 * 计算状态栏颜色
	 *
	 * @param color color值
	 * @param alpha alpha值
	 * @return 最终的状态栏颜色
	 */
	private static int calculateStatusColor(@ColorInt int color, int alpha)
	{
		if (alpha == 0)
		{
			return color;
		}
		float a = 1 - alpha / 255f;
		int red = color >> 16 & 0xff;
		int green = color >> 8 & 0xff;
		int blue = color & 0xff;
		red = (int) (red * a + 0.5);
		green = (int) (green * a + 0.5);
		blue = (int) (blue * a + 0.5);
		return 0xff << 24 | red << 16 | green << 8 | blue;
	}

	/**
	 * 设置 DrawerLayout 属性
	 *
	 * @param drawerLayout              DrawerLayout
	 * @param drawerLayoutContentLayout DrawerLayout 的内容布局
	 */
	private static void setDrawerLayoutProperty(DrawerLayout drawerLayout, ViewGroup drawerLayoutContentLayout)
	{
		ViewGroup drawer = (ViewGroup) drawerLayout.getChildAt(1);
		drawerLayout.setFitsSystemWindows(false);
		drawerLayoutContentLayout.setFitsSystemWindows(false);
		drawerLayoutContentLayout.setClipToPadding(true);
		drawer.setFitsSystemWindows(false);
	}

	/**
	 * 添加半透明矩形条
	 *
	 * @param activity       需要设置的 activity
	 * @param statusBarAlpha 透明值
	 */
	private static void addTranslucentView(Activity activity, int statusBarAlpha)
	{
		ViewGroup contentView = (ViewGroup) activity.findViewById(android.R.id.content);
		View fakeTranslucentView = contentView.findViewById(FAKE_TRANSLUCENT_VIEW_ID);
		if (fakeTranslucentView != null)
		{
			if (fakeTranslucentView.getVisibility() == View.GONE)
			{
				fakeTranslucentView.setVisibility(View.VISIBLE);
			}
			fakeTranslucentView.setBackgroundColor(Color.argb(statusBarAlpha, 0, 0, 0));
		}
		else
		{
			contentView.addView(createTranslucentStatusBarView(activity, statusBarAlpha));
		}
	}

	/**
	 * 设置透明
	 */
	private static void setTransparentForWindow(Activity activity)
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
		{
			activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
			activity.getWindow()
					.getDecorView()
					.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
		}
		else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
		{
			activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		}
	}

	/**
	 * 创建半透明矩形 View
	 *
	 * @param alpha 透明值
	 * @return 半透明 View
	 */
	private static View createTranslucentStatusBarView(Activity activity, int alpha)
	{
		// 绘制一个和状态栏一样高的矩形
		View statusBarView = new View(activity);
		LinearLayout.LayoutParams params =
				new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UIScreenUtil.getStatusHeight(activity));
		statusBarView.setLayoutParams(params);
		statusBarView.setBackgroundColor(Color.argb(alpha, 0, 0, 0));
		statusBarView.setId(FAKE_TRANSLUCENT_VIEW_ID);
		return statusBarView;
	}
}