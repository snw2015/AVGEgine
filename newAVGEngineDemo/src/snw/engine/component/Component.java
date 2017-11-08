package snw.engine.component;

import java.awt.*;
import java.awt.geom.AffineTransform;

import snw.engine.animation.Animation;
import snw.engine.animation.AnimationData;
import snw.engine.database.ImageBufferData;
import snw.math.VectorDbl;
import snw.math.VectorInt;

public abstract class Component {
    protected final String name;
    protected int x;
    protected int y;
    protected int initialX;
    protected int initialY;
    protected int width;
    protected int height;
    private boolean animated = false;
    private boolean visible = true;
    private boolean enable = true;
    private final boolean focusable;

    protected float alpha = 1.0f;

    private AnimationData animationData;
    private Animation animation;

    public static final int ALIGNMENT_LEFTTOP = 0;
    public static final int ALIGNMENT_LEFTBOT = 1;
    public static final int ALIGNMENT_LEFTMID = 2;
    public static final int ALIGNMENT_RIGHTTOP = 3;
    public static final int ALIGNMENT_RIGHTBOT = 4;
    public static final int ALIGNMENT_RIGHTMID = 5;
    public static final int ALIGNMENT_TOPMID = 6;
    public static final int ALIGNMENT_BOTTOMMID = 7;
    public static final int ALIGNMENT_CENTER = 8;

    public static final VectorDbl[] alignmentRatioList = new VectorDbl[]{
            new VectorDbl(0, 0), new VectorDbl(0, 1), new VectorDbl(0, 0.5),
            new VectorDbl(1, 0), new VectorDbl(1, 1), new VectorDbl(1, 0.5),
            new VectorDbl(0.5, 0), new VectorDbl(0.5, 1), new VectorDbl(0.5, 0.5)};

    private int alignment = ALIGNMENT_LEFTTOP;
    protected String[] preLoadImageNames = null;
    private boolean[] preLoaded = null;

    public Component(String name, int x, int y, int width, int height, boolean focusable) {
        this.name = name;
        this.x = x;
        initialX = x;
        this.y = y;
        initialY = y;
        this.width = width;
        this.height = height;
        this.focusable = focusable;
    }

    public Component(String name, int x, int y, int width, int height) {
        this.name = name;
        this.x = x;
        initialX = x;
        this.y = y;
        initialY = y;
        this.width = width;
        this.height = height;
        focusable = true;
    }

    public void preProcess() {
        preLoadImages();
    }

    public void render(Graphics2D g, AnimationData appliedData) {
        AnimationData finalData = getFinalAnimationData().preAdd(appliedData);
        Rectangle bound = trimClip(g);

        paint(g, finalData);

        g.setClip(bound);
    }

    protected Rectangle trimClip(Graphics2D g) {
        Rectangle bound = g.getClipBounds();
        Rectangle clip = getClipOnBound(bound);
        g.setClip(clip.intersection(bound));
        return bound;
    }

    protected Rectangle getClipOnBound(Rectangle bound) {
        Rectangle clip = getClip();
        clip.translate((int) bound.getX(), (int) bound.getY());
        return clip;
    }

    public abstract void paint(Graphics2D g, AnimationData appliedData);

    public void update() {
        updateAnimation();
    }

    public AnimationData getFinalAnimationData() {
        AnimationData finalData = new AnimationData(AffineTransform.getTranslateInstance(getAlignedX(), getAlignedY()));
        finalData.setAlphaFloat(alpha);
        if (animated && animationData != null) {
            finalData.transform(animationData);
        }

        return finalData;
    }

    public void updateAnimation() {
        if (animated && animation != null) {
            animationData = animation.getNextFrame();
        }
    }

    protected void preLoadImages() {
        if (preLoadImageNames != null) {
            preLoaded = new boolean[preLoadImageNames.length];
            for (int i = 0; i < preLoadImageNames.length; i++) {
                if (ImageBufferData.storeImage(preLoadImageNames[i])) {
                    preLoaded[i] = true;
                } else {
                    preLoaded[i] = false;
                }
            }
        }
    }


    protected Image getImage(String name) {
        return (ImageBufferData.getImage(name));
    }

    private void releaseImages() {
        if (preLoadImageNames != null) {
            for (int i = 0; i < preLoadImageNames.length; i++) {
                if (preLoaded[i]) {
                    ImageBufferData.releaseImage(preLoadImageNames[i]);
                }
            }
        }
    }

    public void exit() {
        releaseImages();
    }

    public void keyTyped(char keyChar) {

    }

    public void keyPressed(int key) {
    }

    public void keyReleased(int key) {
    }

    public void mouseClicked(int mouseX, int mouseY) {
    }

    public void mousePressed(int mouseX, int mouseY) {
    }

    public void mouseReleased(int mouseX, int mouseY) {
    }

    public void mouseEntered() {
        //print("move into " + name);
    }

    public void mouseExited() {
        //print("move out of " + name);
    }

    public boolean mouseMoved(int mouseX, int mouseY) {
        return false;
    }

    public void mouseDragged(int mouseX, int mouseY) {
    }

    public void setPaintedPosition(int startX, int startY) {
    }

    public String getName() {
        return name;
    }

    public VectorInt getPosVec() {
        return (new VectorInt(x, y));
    }

    public static void print(Object s) {
        System.out.println(s);
    }

    public int getX() {
        return x;
    }

    public int getX(int deltaX) {
        return initialX + deltaX;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public int getY(int deltaY) {
        return initialY + deltaY;
    }

    public void setY(int y) {
        this.y = y;
    }

    public VectorInt getPos() {
        return (new VectorInt(x, y));
    }

    public Rectangle getBound() {
        return (new Rectangle(getAlignedX(), getAlignedY(), width, height));
    }

    public void setPos(VectorInt pos) {
        setX(pos.x);
        setY(pos.y);
    }

    public void setPos(int x, int y) {
        setX(x);
        setY(y);
    }

    public Animation getAnimation() {
        return animation;
    }

    public void setAnimation(Animation animation) {
        // System.out.println("Set Animation: " + animation);
        synchronized (this) {
            if (animation != null) {
                this.animation = animation;
                animationData = animation.getFirstFrame();
                animated = true;
            } else {
                this.animation = null;
                animationData = null;
                animated = false;
            }
        }
    }

    public void setAnimation(Animation animation, boolean isLoop) {
        setAnimation(animation);
        animation.setLoop(isLoop);
    }

    public Rectangle getClip() {
        return new Rectangle(getAlignedX(), getAlignedY(), width, height);
    }

    public Shape getClip(Shape boundClip) {
        if (boundClip instanceof Rectangle) {
            Rectangle absoluteClip = new Rectangle(getClip());
            absoluteClip.translate((int) ((Rectangle) boundClip).getX(), (int) ((Rectangle) boundClip).getY());

            //print(name);
            //print("re clip:"+getClip()+"ab clip:"+absoluteClip+"bound: "+boundClip);
            //print("answer:"+((Rectangle) boundClip).intersection(absoluteClip));
            //print("");

            return (((Rectangle) boundClip).intersection(absoluteClip));
        } else {
            //TODO
        }
        return boundClip;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        if (width > 0) {
            this.width = width;
        } else {
            this.width = 1;
        }
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        if (height > 0) {
            this.height = height;
        } else {
            this.height = 1;
        }
    }

    public void setSize(int width, int height) {
        setWidth(width);
        setHeight(height);
    }

    public void setSize(VectorInt size) {
        setWidth(size.x);
        setHeight(size.y);
    }

    public VectorInt getSize() {
        return (new VectorInt(width, height));
    }

    public boolean isAnimated() {
        return animated;
    }

    public int getAlignment() {
        return alignment;
    }

    public int getXDelta() {
        return (int) ((double) width * alignmentRatioList[alignment].x);
    }

    public int getAlignedX() {
        return (x - getXDelta());
    }

    public int getYDelta() {
        return (int) ((double) height * alignmentRatioList[alignment].y);
    }

    public int getAlignedY() {
        return (y - getYDelta());
    }

    public void setAlignment(int alignment) {
        this.alignment = alignment;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean isVisible) {
        this.visible = isVisible;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean isEnable) {
        this.enable = isEnable;
    }

    public boolean isFocusable() {
        return focusable;
    }

    public AnimationData getAnimationData() {
        return animationData;
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public String toString() {
        return name;
    }
}
