package it.instruman.treasurecruisedatabase.nakama.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;

import java.util.EnumSet;
import java.util.Iterator;

import it.instruman.treasurecruisedatabase.R;

/**
 * Created by infan on 19/02/2018.
 */

public class BitmapComposer {
    // Relative size of the sub images inside the main image (3 means 1/3 )
    private static int SUBIMAGES_PROPORTION = 3;

    // Relative size of the margin around the center image (5 means 1/5)
    private static int CENTERIMAGE_PROPORTION = 5;

    public static Bitmap composeBitmap(Context context, CommunicationHandler.UnitRole roleFlag, CommunicationHandler.UnitType typeFlag, EnumSet<CommunicationHandler.UnitClass> classFlags) {
        Bitmap baseBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_empty_slot);

        int baseBmpWidth = baseBitmap.getWidth();
        int baseBmpHeight = baseBitmap.getHeight();

        Bitmap finalBitmap = Bitmap.createBitmap(baseBmpWidth, baseBmpHeight, baseBitmap.getConfig());
        Canvas canvas = new Canvas(finalBitmap);

        canvas.drawBitmap(baseBitmap, 0f, 0f, null);
        if(roleFlag != CommunicationHandler.UnitRole.Unknown) {
            String roleBitmapResName = "ic_roles_" + roleFlag.getValue();
            int roleBitmapResId = getDrawableResIdFromName(context, roleBitmapResName);
            Bitmap roleBitmap = BitmapFactory.decodeResource(context.getResources(), roleBitmapResId);
            canvas.drawBitmap(roleBitmap, null, new RectF(baseBmpWidth/CENTERIMAGE_PROPORTION, baseBmpHeight/CENTERIMAGE_PROPORTION, baseBmpWidth-baseBmpWidth/CENTERIMAGE_PROPORTION, baseBmpHeight-baseBmpHeight/CENTERIMAGE_PROPORTION), null);
        }
        if(typeFlag != CommunicationHandler.UnitType.Unknown) {
            String typeBitmapResName = "ic_types_" + typeFlag.getValue();
            int typeBitmapResId = getDrawableResIdFromName(context, typeBitmapResName);
            Bitmap typeBitmap = BitmapFactory.decodeResource(context.getResources(), typeBitmapResId);
            canvas.drawBitmap(typeBitmap, null, new RectF(0, 0, baseBmpWidth/SUBIMAGES_PROPORTION, baseBmpHeight/SUBIMAGES_PROPORTION), null);
        }
        switch(classFlags.size()) {
            case 1: {
                CommunicationHandler.UnitClass flag1 = classFlags.iterator().next();
                if(flag1!= CommunicationHandler.UnitClass.Unknown) {
                    String classBitmapResName = "ic_classes_" + flag1.getValue();
                    int classBitmapResId = getDrawableResIdFromName(context, classBitmapResName);
                    Bitmap classBitmap = BitmapFactory.decodeResource(context.getResources(), classBitmapResId);
                    canvas.drawBitmap(classBitmap, null, new RectF(baseBmpWidth - baseBmpWidth / SUBIMAGES_PROPORTION, baseBmpHeight - baseBmpHeight / SUBIMAGES_PROPORTION, baseBmpWidth, baseBmpHeight), null);
                }
                break;
            }
            case 2:
                Iterator<CommunicationHandler.UnitClass> iterator = classFlags.iterator();

                CommunicationHandler.UnitClass flag1 = iterator.next();
                if(flag1!= CommunicationHandler.UnitClass.Unknown) {
                    String classBitmapResName = "ic_classes_" + flag1.getValue();
                    int classBitmapResId = getDrawableResIdFromName(context, classBitmapResName);
                    Bitmap classBitmap = BitmapFactory.decodeResource(context.getResources(), classBitmapResId);
                    canvas.drawBitmap(classBitmap, null, new RectF(baseBmpWidth - baseBmpWidth / SUBIMAGES_PROPORTION, baseBmpHeight - baseBmpHeight / SUBIMAGES_PROPORTION, baseBmpWidth, baseBmpHeight), null);
                }

                CommunicationHandler.UnitClass flag2 = iterator.next();
                if(flag2!= CommunicationHandler.UnitClass.Unknown) {
                    String classBitmapResName2 = "ic_classes_" + flag2.getValue();
                    int classBitmapResId2 = getDrawableResIdFromName(context, classBitmapResName2);
                    Bitmap classBitmap2 = BitmapFactory.decodeResource(context.getResources(), classBitmapResId2);
                    canvas.drawBitmap(classBitmap2, null, new RectF(0, baseBmpHeight - baseBmpHeight / SUBIMAGES_PROPORTION, baseBmpWidth / SUBIMAGES_PROPORTION, baseBmpHeight), null);
                }
                break;
        }
        return finalBitmap;
    }

    private static int getDrawableResIdFromName(Context context, String drawableName) {
        String packageName = context.getPackageName();
        return context.getResources().getIdentifier(drawableName, "drawable", packageName);
    }
}
