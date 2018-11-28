package com.webank.mbank.circle;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.SweepGradient;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by jasoncai on 2018/11/12.
 */

public class CircleView extends View {

    private static final String TAG = "CircleView";


    /**1、各种属性的默认值：
     *
     * 白色圆对应的阴影颜色
     */
    private static final int DF_WHITE_CIRCLE_SHADOW_COLOR = Color.parseColor("#5192FF");
    /**
     * 红色圆对应的阴影颜色
     */
    private static final int DF_RED_CIRCLE_SHADOW_COLOR = Color.parseColor("#FF5151");
    /**
     * 透明度为100%的白色
     */
    private static final int DF_WHITE_100 = Color.parseColor("#FFFFFFFF");
    /**
     * 透明度为100%的红色
     */
    private static final int DF_RED_100 = Color.parseColor("#FFFF7777");
    /**
     * 透明度为100%的浅红色
     */
    private static final int DF_LIGHT_RED_100 = Color.parseColor("#FFFFA5A5");
    /**
     * 透明度为50%的灰色
     */
    private static final int DF_BACKGROUND_COLOR = Color.parseColor("#7F0A0834");
    /**
     * 透明度为30%的白色
     */
    private static final int DF_WHITE_30 = Color.parseColor("#4CFFFFFF");
    /**
     * 透明度为30%的红色
     */
    private static final int DF_RED_30 = Color.parseColor("#4CFF7777");
    /**
     * 缩放动画的最大放大比例
     */
    public static final float DF_MAX_SCALE = 2f;
    /**
     * 设计图的最内部圆大小
     */
    public static final int DF_ORIGIN_RADIUS = 256;
    /**
     * 每一帧旋转的角度
     */
    public static final int DF_ROTATE_ANGLE_STEP = 3;
    /**
     * 缩放动画的半径改变的步长
     */
    public static final float DF_RADIUS_CHANGE_STEP = (float) 10;
    /**
     * 圆心半径比值改变的阈值
     */
    public static final float DF_RADIUS_THRESHOLD = (float) 10;



    /**
     * 2、BORDER_WIDTH_X表示不同的圆/圆弧的边宽
     */
    private static final int BORDER_WIDTH_1 = 1;
    private static final int BORDER_WIDTH_2 = 2;
    private static final int BORDER_WIDTH_3 = 3;
    private static final int BORDER_WIDTH_4 = 4;
    private static final int BORDER_WIDTH_6 = 6;
    private static final int BORDER_WIDTH_10 = 10;

    /**
     * 3、ALPAH_LEVEL_X表示不同%的透明度
     */
    public static final int ALPAH_LEVEL_30 = 77;
    public static final int ALPAH_LEVEL_56 = 142;
    public static final int ALPAH_LEVEL_60 = 153;
    public static final int ALPAH_LEVEL_70 = 178;
    public static final int ALPAH_LEVEL_100 = 255;

    /**
     * 4、SCALE_X_TO_CIRCLE1表示X相对于CIRCLE1的半径比值,这些值来源于设计图
     */
    public static final double SCALE_CIRCLE2_TO_CIRCLE1 = 1.0820;
    public static final double SCALE_CIRCLE3_TO_CIRCLE1 = 1.1992;
    public static final double SCALE_ARC1_TO_CIRCLE1 = 1.0664;
    public static final double SCALE_ARC2_TO_CIRCLE1 = 1.1172;
    public static final double SCALE_ARC3_TO_CIRCLE1 = 1.1641;


    /**
     * 5、radiusCircleX表示依据基准半径256，从内到外的圆半径
     */
    private float radiusCircle1 = DF_ORIGIN_RADIUS;
    private float radiusCircle2 = (float) (radiusCircle1 * SCALE_CIRCLE2_TO_CIRCLE1);
    private float radiusCircle3 = (float) (radiusCircle1 * SCALE_CIRCLE3_TO_CIRCLE1);

    /**
     * 6、radiusArcX依据基准半径256，依次从内到外的圆弧半径
     */
    private float radiusArc1 = (float) (radiusCircle1 * SCALE_ARC1_TO_CIRCLE1);
    private float radiusArc2 = (float) (radiusCircle1 * SCALE_ARC2_TO_CIRCLE1);
    private float radiusArc3 = (float) (radiusCircle1 * SCALE_ARC3_TO_CIRCLE1);
    private float radiusArc4 = radiusCircle3;

    /**
     * 7、各种自定义属性
     */
    private int circleColor = DF_WHITE_100;
    /**
     * 背景颜色
     */
    private int backGroundColor;
    /**
     * 缩放动画的最大放大比例
     */
    private float maxScale;
    /**
     * 缩放动画改变的步长
     */
    private float radiusChangeStep;
    /**
     * 最小圆半径改变的阈值
     */
    private float radiusThreshold;
    /**
     * 圆的阴影颜色
     */
    private int shadowColor = DF_WHITE_CIRCLE_SHADOW_COLOR;

    /**
     * 8、
     * 圆和圆弧的各种画笔
     * 其中：
     * minCirclePaint表示最小圆的画笔
     * minARcPaint表示最小圆弧的画笔
     */
    private Paint minCirclePaint;
    private Paint midCirclePaint;
    private Paint maxCirclePaint;

    private Paint minARcPaint;
    private Paint midARcPaint;
    private Paint maxARcPaint;
    private Paint largeARcPaint;


    /**9、圆的各种信息：
     * 最内部圆心X坐标
     */
    private float cx;
    /**
     * 最内部圆心Y坐标
     */
    private float cy;
    /**
     * 实时传进来的半径
     */
    private float radius;


    /**
     * 10、辅助画圆和圆弧的中间变量：
     *
     * 限制圆弧边界的外接矩形
     */
    private RectF oval;
    /**
     * 裁剪区域的路径
     */
    private Path mPath;
    /**
     * 用于产生虚线圆Effect
     */
    private DashPathEffect mDashPathEffect;
    /**
     * 白颜色的渐变数组
     */
    private int[] mWhiteColors;
    /**
     * 红颜色的渐变数组
     */
    private int[] mRedColors;
    /**
     * 颜色数组中颜色出现的位置
     */
    private float[] mPositions;
    /**
     * 虚线圆弧的长度和间隔数组
     */
    private float[] mFloats;

    /**
     * 缩放动画中最初始的圆半径
     */
    private float toScaleRadius = 0;

    /**
     * 旋转角度
     */
    private float rotateAngle;
    /**
     * 旋转改变角度步长
     */
    private float rotateAngleStep;

    /**
     * 通过Handler不断重绘（也可以通过定时器）
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            invalidate();
        }
    };


    public CircleView(Context context) {
        super(context);
        init(context, null);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            @SuppressLint("Recycle") TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleView);
            rotateAngleStep = typedArray.getInteger(R.styleable.CircleView_rotate_angle_step, DF_ROTATE_ANGLE_STEP);
            maxScale = typedArray.getFloat(R.styleable.CircleView_scale_max, DF_MAX_SCALE);
            radiusChangeStep = typedArray.getFloat(R.styleable.CircleView_scale_radius_change_step, DF_RADIUS_CHANGE_STEP);
            radiusThreshold = typedArray.getFloat(R.styleable.CircleView_scale_radius_threshold, DF_RADIUS_THRESHOLD);

            //获取背景颜色值，如果为空，值设置为默认颜色
            String color = typedArray.getString(R.styleable.CircleView_back_ground_color);
            if (color != null) {
                backGroundColor = Color.parseColor(color);
            } else {
                backGroundColor = DF_BACKGROUND_COLOR;
            }

            WeBankLogger.d(TAG, "rotateAngleStep=" + rotateAngleStep + ",maxScale=" + maxScale +
                    ",radiusChangeStep=" + radiusChangeStep + ",radiusThreshold=" + radiusThreshold);
        }
    }


    /**
     * 为了处理wrap_content情况
     *（MeasureSpe=SpecMode+SpecSize）
     * @param widthMeasureSpec  宽的测试规格
     * @param heightMeasureSpec 高的测量规格
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int minWidth = 500;
        int minHeight = 500;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(minWidth, minHeight);
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(minWidth, heightSpecSize);
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, minHeight);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        //只有初始化传入圆信息和view未被消灭时才开始动画
        if (!isDetached && isInitHandler) {

            //设置画布的裁剪区域为最小圆的外部，也就是只有在裁剪区域之外的可以显示出来
            getPath().addCircle(cx, cy, radiusCircle1, Path.Direction.CCW);
            canvas.clipPath(mPath, Region.Op.DIFFERENCE);

            //绘制背景
            canvas.drawColor(backGroundColor);
            //绘制圆形
            drawCircle(canvas);
            //绘制圆弧
            drawArc(canvas);
            //更新每次转动角度
            rotateAngle = rotateAngle + rotateAngleStep;
            //更新缩放中圆的半径
            if (toScaleRadius - radius > radiusChangeStep) {
                toScaleRadius = (toScaleRadius - radiusChangeStep);
            } else {
                toScaleRadius = radius;
            }
            resetAllRadius(radius);
            mHandler.sendEmptyMessageDelayed(0, 20);

        } else {
            //初始化或者停止动画时，只绘制背景
            canvas.drawColor(backGroundColor);
        }
    }

    /**
     * type=1,2,3分表表示从内到外的三个圆
     *
     * @param canvas 画布
     */
    private void drawCircle(Canvas canvas) {

        //利用path绘制第一个圆
        canvas.drawPath(mPath, getMinCirclePaint());

        //绘制中间的圆
        canvas.drawCircle(cx, cy, radiusCircle2, getMidCirclePaint());

        //旋转画布，让整个圆转起来，绘制最外层的圆
        canvas.save();
        canvas.rotate(-rotateAngle, cx, cy);
        canvas.drawCircle(cx, cy, radiusCircle3, getMaxCirclePaint());
        canvas.restore();

    }

    private void drawArc(Canvas canvas) {
        //画半径为radiusArc1的四段粗圆弧
        oval = getRectF(cx - radiusArc1, cy - radiusArc1, cx + radiusArc1, cy + radiusArc1);
        canvas.drawArc(oval, -90 - rotateAngle, 20, false, getMinARcPaint());
        canvas.drawArc(oval, 0 - rotateAngle, 20, false, getMinARcPaint());
        canvas.drawArc(oval, 90 - rotateAngle, 20, false, getMinARcPaint());
        canvas.drawArc(oval, 180 - rotateAngle, 20, false, getMinARcPaint());

        //画半径为radiusArc2的两段虚线圆弧
        oval = getRectF(cx - radiusArc2, cy - radiusArc2, cx + radiusArc2, cy + radiusArc2);
        canvas.drawArc(oval, -90 + rotateAngle, 90, false, getMidARcPaint());
        canvas.drawArc(oval, 90 + rotateAngle, 90, false, getMidARcPaint());

        //画半径为radiusArc3的两段刻度线圆弧
        oval = getRectF(cx - radiusArc3, cy - radiusArc3, cx + radiusArc3, cy + radiusArc3);
        canvas.drawArc(oval, -90 - rotateAngle, 90, false, getMaxARcPaint());
        canvas.drawArc(oval, 90 - rotateAngle, 90, false, getMaxARcPaint());

        //画半径为radiusArc4的四段端细实线圆弧
        oval = getRectF(cx - radiusArc4, cy - radiusArc4, cx + radiusArc4, cy + radiusArc4);
        canvas.drawArc(oval, -90 + rotateAngle, 22.5f, false, getLargeARcPaint());
        canvas.drawArc(oval, 0 + rotateAngle, 22.5f, false, getLargeARcPaint());
        canvas.drawArc(oval, 90 + rotateAngle, 22.5f, false, getLargeARcPaint());
        canvas.drawArc(oval, 180 + rotateAngle, 22.5f, false, getLargeARcPaint());

    }


    private boolean isInitHandler;

    /**
     * 此方法用于外部设置最小圆的半径和圆心坐标,也是动画开始的方法
     *
     * @param cx     圆心X坐标
     * @param cy     圆心Y坐标
     * @param radius 圆心半径
     */
    public void setCircleAndDraw(float cx, float cy, float radius) {

        //初始化Handler
        if (!isInitHandler) {
            isInitHandler = true;
            mHandler.sendEmptyMessage(0);
        }
        this.cx = cx;
        this.cy = cy;
        this.radius = radius;
        resetAllRadius(radius);
    }

    /**
     * 更新所有圆和圆弧的半径
     */

    private float lastRadius;

    private void resetAllRadius(float radius) {
        if (toScaleRadius == 0 || lastRadius == 0 || Math.abs(lastRadius - radius) > radiusThreshold) {
            toScaleRadius = radius * maxScale;
            WeBankLogger.d(TAG, "Scale Animation Restart!!!");
        }
        radiusCircle1 = toScaleRadius;
        radiusCircle2 = (float) (radiusCircle1 * SCALE_CIRCLE2_TO_CIRCLE1);
        radiusCircle3 = (float) (radiusCircle1 * SCALE_CIRCLE3_TO_CIRCLE1);

        radiusArc1 = (float) (radiusCircle1 * SCALE_ARC1_TO_CIRCLE1);
        radiusArc2 = (float) (radiusCircle1 * SCALE_ARC2_TO_CIRCLE1);
        radiusArc3 = (float) (radiusCircle1 * SCALE_ARC3_TO_CIRCLE1);
        radiusArc4 = radiusCircle3;
        lastRadius = radius;
        WeBankLogger.d(TAG, "radiusCircle1=" + radiusCircle1);

    }

    /**
     * 动画停止
     */
    public void stopAnimation() {
        reset();
        invalidate();
    }

    /**
     * 这个方法主要是为了复用oval，减少内存的消耗
     */
    private RectF getRectF(float left, float top, float right, float bottom) {
        if (oval == null) {
            oval = new RectF(cx - radiusArc1, cy - radiusArc1, cx + radiusArc1, cy + radiusArc1);
        }
        oval.left = left;
        oval.top = top;
        oval.right = right;
        oval.bottom = bottom;
        return oval;
    }

    /**
     * 这个方法主要是为了复用float[]，减少内存的消耗
     */
    private float[] getFloatArray(float x, float y) {
        if (mFloats == null) {
            mFloats = new float[]{x, y};
        }
        mFloats[0] = x;
        mFloats[1] = y;
        return mFloats;

    }

    /**
     * 获取绘制的路径
     *
     * @return 需要绘制的路径
     */
    private Path getPath() {
        if (mPath == null) {
            mPath = new Path();
        }
        mPath.reset();
        return mPath;
    }

    /**
     * 自定义控件是否脱离窗体
     */
    private boolean isDetached;

    /**
     * 当自定义控件脱离窗体，即将销毁
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isDetached = true;
        reset();

    }

    /**
     * 此方法必须在调用setCircleAndDraw之前调用，默认颜色为白色"#FFFFFFFF"
     *
     * @param color 重新给圆设置的颜色
     */
    public void setCircleColor(CircleColor color) {
        switch (color) {
            case WHITE:
                circleColor = DF_WHITE_100;
                shadowColor = DF_WHITE_CIRCLE_SHADOW_COLOR;
                break;
            case RED:
                circleColor = DF_RED_100;
                shadowColor = DF_RED_CIRCLE_SHADOW_COLOR;
                break;
            default:
                break;
        }
    }

    /**
     * 重置变量
     */
    private void reset() {
        toScaleRadius = 0;
        lastRadius = 0;
        isInitHandler = false;
        mHandler.removeCallbacksAndMessages(null);
    }

    /**
     * 构造颜色数组
     *
     * @return 返回颜色数组
     */
    private int[] getColors() {
        if (circleColor == DF_WHITE_100) {
            if (mWhiteColors == null) {
                mWhiteColors = new int[]{
                        DF_WHITE_30,
                        DF_WHITE_30, DF_WHITE_100, DF_WHITE_100, DF_WHITE_30,
                        DF_WHITE_30, DF_WHITE_100, DF_WHITE_100, DF_WHITE_30,
                        DF_WHITE_30, DF_WHITE_100, DF_WHITE_100, DF_WHITE_30,
                        DF_WHITE_30, DF_WHITE_100, DF_WHITE_100, DF_WHITE_30,
                        DF_WHITE_30};
            }
            return mWhiteColors;
        } else {
            //红色圆下最外层圆的颜色值跟其他红色圆不一样
            if (mRedColors == null) {
                mRedColors = new int[]{
                        DF_RED_30,
                        DF_RED_30, DF_RED_100, DF_RED_100, DF_RED_30,
                        DF_RED_30, DF_RED_100, DF_RED_100, DF_RED_30,
                        DF_RED_30, DF_RED_100, DF_RED_100, DF_RED_30,
                        DF_RED_30, DF_RED_100, DF_RED_100, DF_RED_30,
                        DF_RED_30};
            }
            return mRedColors;
        }


    }

    /**
     * 构建颜色数组对应的位置数组
     *
     * @return 返回颜色位置数组
     */
    private float[] getPositions() {
        if (mPositions == null) {
            mPositions = new float[]{0f,
                    0.08f, 0.10f, 0.14f, 0.16f,
                    0.33f, 0.35f, 0.39f, 0.41f,
                    0.58f, 0.60f, 0.64f, 0.66f,
                    0.83f, 0.85f, 0.89f, 0.91f,
                    1.0f};
        }
        return mPositions;
    }

    /**
     * 获取绘制圆形（圆弧）的画笔。
     * 因为复用画笔涉及到频繁调用native方法，费时，故创建4个不同画笔更快捷
     *
     * @return Paint
     */
    private Paint getMinCirclePaint() {
        if (minCirclePaint == null) {
            minCirclePaint = new Paint();
            minCirclePaint.setStyle(Paint.Style.STROKE);
            minCirclePaint.setAntiAlias(true);
            minCirclePaint.setDither(true);
            minCirclePaint.setStrokeWidth(BORDER_WIDTH_3);
        }
        //因为涉及到阴影颜色的动态改变，所以也不能复用
        minCirclePaint.setShadowLayer(6, 0, 0, shadowColor);
        //因为涉及到外部动态修改画笔颜色，所以这个不可以复用
        minCirclePaint.setColor(circleColor);
        //透明度设置必须放在颜色设置之后才有效
        minCirclePaint.setAlpha(ALPAH_LEVEL_100);

        return minCirclePaint;

    }

    private Paint getMidCirclePaint() {
        if (midCirclePaint == null) {
            midCirclePaint = new Paint();
            midCirclePaint.setStyle(Paint.Style.STROKE);
            midCirclePaint.setAntiAlias(true);
            midCirclePaint.setDither(true);
            midCirclePaint.setStrokeWidth(BORDER_WIDTH_3);
        }
        midCirclePaint.setShadowLayer(12, 0, 0, shadowColor);
        midCirclePaint.setColor(circleColor);
        midCirclePaint.setAlpha(ALPAH_LEVEL_56);
        return midCirclePaint;
    }

    private Paint getMaxCirclePaint() {
        if (maxCirclePaint == null) {
            maxCirclePaint = new Paint();
            maxCirclePaint.setStyle(Paint.Style.STROKE);
            maxCirclePaint.setAntiAlias(true);
            maxCirclePaint.setDither(true);
            maxCirclePaint.setStrokeWidth(BORDER_WIDTH_1);

        }
        //涉及到getColors变化，也要放到外面进行动态更新
        SweepGradient sweepGradient = new SweepGradient(cx, cy, getColors(), getPositions());
        maxCirclePaint.setShader(sweepGradient);
        return maxCirclePaint;
    }


    private Paint getMinARcPaint() {
        if (minARcPaint == null) {
            minARcPaint = new Paint();
            minARcPaint.setStyle(Paint.Style.STROKE);
            minARcPaint.setAntiAlias(true);
            minARcPaint.setDither(true);
            minARcPaint.setStrokeWidth(BORDER_WIDTH_10);
        }
        minARcPaint.setShadowLayer(12, 0, 0, shadowColor);
        if (circleColor == DF_WHITE_100) {
            minARcPaint.setColor(circleColor);
        } else {
            //红色下的第一段圆弧使用另外一种颜色
            minARcPaint.setColor(DF_LIGHT_RED_100);
        }
        minARcPaint.setAlpha(ALPAH_LEVEL_100);
        return minARcPaint;
    }

    private Paint getMidARcPaint() {
        if (midARcPaint == null) {
            midARcPaint = new Paint();
            midARcPaint.setStyle(Paint.Style.STROKE);
            midARcPaint.setAntiAlias(true);
            midARcPaint.setDither(true);
            midARcPaint.setStrokeWidth(BORDER_WIDTH_2);
            mDashPathEffect = new DashPathEffect(getFloatArray(4, 8), 0);
            midARcPaint.setPathEffect(mDashPathEffect);
        }
        midARcPaint.setColor(circleColor);
        if (shadowColor == DF_WHITE_CIRCLE_SHADOW_COLOR) {
            midARcPaint.setAlpha(ALPAH_LEVEL_60);
        } else {
            midARcPaint.setAlpha(ALPAH_LEVEL_70);
        }
        return midARcPaint;
    }

    private Paint getMaxARcPaint() {
        if (maxARcPaint == null) {
            maxARcPaint = new Paint();
            maxARcPaint.setStyle(Paint.Style.STROKE);
            maxARcPaint.setAntiAlias(true);
            maxARcPaint.setDither(true);

            maxARcPaint.setStrokeWidth(BORDER_WIDTH_6);
            mDashPathEffect = new DashPathEffect(getFloatArray(2, 9), 0);
            maxARcPaint.setPathEffect(mDashPathEffect);
        }
        maxARcPaint.setColor(circleColor);
        if (shadowColor == DF_WHITE_CIRCLE_SHADOW_COLOR) {
            maxARcPaint.setAlpha(ALPAH_LEVEL_30);
        } else {
            maxARcPaint.setAlpha(ALPAH_LEVEL_70);
        }
        return maxARcPaint;
    }

    private Paint getLargeARcPaint() {
        if (largeARcPaint == null) {
            largeARcPaint = new Paint();
            largeARcPaint.setStyle(Paint.Style.STROKE);
            largeARcPaint.setAntiAlias(true);
            largeARcPaint.setDither(true);

            largeARcPaint.setStrokeWidth(BORDER_WIDTH_4);

        }
        largeARcPaint.setShadowLayer(10, 0, 0, shadowColor);
        if (circleColor == DF_WHITE_100) {
            largeARcPaint.setColor(circleColor);
        } else {
            largeARcPaint.setColor(DF_LIGHT_RED_100);
        }
        largeARcPaint.setAlpha(ALPAH_LEVEL_100);
        return largeARcPaint;
    }

    public enum CircleColor {
        /**
         * 白色
         */
        WHITE,
        /**
         * 红色
         */
        RED
    }
}