package org.chuck.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import org.chuck.R;

/**
 * 该EditText右侧带有一个删除图标，点击该图标后会清除该编辑框的内容
 * 
 * 该EditText还可以通过设置一个验证器，在该EditText失去焦点的时候验证该EditText的内容是否是正确的，
 * 正确的时候可以再右边显示一个对号来标识该表单域是正确的，错误的话可以将该EditText的背景变成红色来提示用户。 设置EditText的方式如下：
 * 
 * <pre>
 * etPhoneNum.setContentValidator(new EditTextContentValidator() {
 * 
 * 	public boolean validate(ClearableEditText targetView, String str) {
 * 		// 该处写验证规则
 * 		return false;
 * 	}
 * 
 * 	public void onValidateRight(ClearableEditText targetView) {
 * 		// 该处写符合验证规则是targetView的视图样式
 * 	}
 * 
 * 	public void onValidateError(ClearableEditText targetView) {
 * 		// 该处写不符合验证规则是targetView的视图样式
 * 	}
 * });
 * </pre>
 * 
 * 该项目中可以简写为：
 * 
 * <pre>
 * etPhoneNum.setContentValidator(new CommonEditTextContentValidator() {
 * 	public boolean validate(ClearableEditText targetView, String str) {
 * 
 * 		return CebFormValidator.isEditTextValidPhoneNum(targetView);
 * 
 * 	}
 * });
 * </pre>
 * 
 * 具体的逻辑请看源码
 * 
 * @author gaobingbing
 */
public class ClearableEditText extends EditText {

	/** 图标触控区域控制 */
	private int touchOffsetX;

	/** 图标大小控制 */
	private int offsetY;

	/** 右侧位图 **/
	private Drawable rightDrawable;

	/** 右侧位图大小 **/
	private int drawableSize;

	/** 点击右侧位图的监听 **/
	private OnClickListener drawableClickListener;

	/** 该EditText的验证器 */
	private EditTextContentValidator contentValidator;

	/** 清除输入内容监听器 */
	private OnClickListener clearListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			EditText etTargetView = (EditText) view;
			etTargetView.setText("");
		}
	};

	public ClearableEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		initAttr(context, attrs);
	}

	public ClearableEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initAttr(context, attrs);
	}

	private void initAttr(Context context, AttributeSet attrs) {
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ClearEditText);
		rightDrawable = typedArray.getDrawable(R.styleable.ClearEditText_right_drawable);
		touchOffsetX = typedArray.getDimensionPixelSize(R.styleable.ClearEditText_touch_offset_x, 0);
		offsetY = typedArray.getDimensionPixelSize(R.styleable.ClearEditText_offset_y, 0);
		typedArray.recycle();

		this.addTextChangedListener(new EditTextTextWatcher());
		setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					if (!TextUtils.isEmpty(getText().toString())) {
						setDrawableAndClickListener(rightDrawable, clearListener);
					}
				} else {// 失去焦点，验证输入内容
					setCompoundDrawables(null, null, null, null);
					if (contentValidator != null) {
						if (contentValidator.validate(ClearableEditText.this, getText().toString())) {
							contentValidator.onValidateRight(ClearableEditText.this);
						} else {
							contentValidator.onValidateError(ClearableEditText.this);
						}
					}
				}
			}
		});
	}

	/**
	 * 设置右侧图标显示并给予监听,若该方法未被调用，则右侧位图默认为删除图标
	 * 
	 * @param rightDrawable 右侧图标
	 * @param drawableClickListener 图标对应的监听
	 */
	public void setDrawableAndClickListener(Drawable rightDrawable, OnClickListener drawableClickListener) {
		this.rightDrawable = rightDrawable;
		this.drawableClickListener = drawableClickListener;
		if (rightDrawable != null) {
			// 计算右侧位图大小并设置显示
			int actualHeight = drawableSize = getHeight() - getPaddingTop() - getPaddingBottom();
			if (offsetY > 0 && offsetY < actualHeight) {
				drawableSize -= offsetY;
			}
			// 留和drawable相同大小的空间
			rightDrawable.setBounds(0, 0, drawableSize, drawableSize);
			this.setCompoundDrawables(null, null, rightDrawable, null);
		}
	}

	/**
	 * 设置输入内容验证器
	 * 
	 * @param contentValidator 内容验证器
	 */
	public void setContentValidator(EditTextContentValidator contentValidator) {
		this.contentValidator = contentValidator;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP && rightDrawable != null) {
			int x = (int) event.getX();
			// 此处限定右侧图标（删除图标）在X轴的触控区域，Y轴不做限定
			if (x >= (this.getWidth() - (drawableSize + this.getPaddingRight() + touchOffsetX)) && x <= (this.getWidth() - (this.getPaddingRight() - touchOffsetX))) {
				if (drawableClickListener != null) {
					drawableClickListener.onClick(this);
					this.setCompoundDrawables(null, null, null, null);
				}
				event.setAction(MotionEvent.ACTION_CANCEL);// use this to
			}
		}
		return super.onTouchEvent(event);
	}

	/**
	 * <pre>
	 *  ClearableEditText的专用TextWatcher，
	 *  若要在外边用addTextChangedListener(xxxTextWatcher)，
	 *  xxxTextWatcher必须继承EditTextTextWatcher并在onTextChanged()方法中调用super.onTextChanged()，否则该组件无效;
	 * </pre>
	 */
	public class EditTextTextWatcher implements TextWatcher {

		@Override
		public void afterTextChanged(Editable s) {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if (!hasFocus()) {
				return;
			}
			if (!TextUtils.isEmpty(s.toString())) {
				setDrawableAndClickListener(rightDrawable, clearListener);
			} else {
				setCompoundDrawables(null, null, null, null);
			}
		}

	}

	/**
	 * 该EditText的验证器
	 */
	public interface EditTextContentValidator {

		boolean validate(ClearableEditText targetView, String str);

		void onValidateRight(ClearableEditText targetView);

		void onValidateError(ClearableEditText targetView);
	}

	/**
	 * 通用的验证器，只实现了验证正确和验证错误时View的外观
	 */
	public static abstract class CommonEditTextContentValidator implements EditTextContentValidator {

		@Override
		public abstract boolean validate(ClearableEditText targetView, String str);

		@Override
		public void onValidateRight(ClearableEditText targetView) {
			Drawable validateRightDrawable = targetView.getContext().getResources().getDrawable(R.drawable.validate_right);
			targetView.setDrawableAndClickListener(validateRightDrawable, null);
			targetView.setBackgroundResource(R.drawable.form_edit_text_bg_normal);
		}

		@Override
		public void onValidateError(ClearableEditText targetView) {
			targetView.setBackgroundResource(R.drawable.form_edit_text_bg_error);
		}
	}

}
