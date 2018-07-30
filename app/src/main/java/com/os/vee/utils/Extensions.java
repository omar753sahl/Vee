package com.os.vee.utils;

import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.square1.richtextlib.ui.RichContentView;
import io.square1.richtextlib.v2.RichTextV2;
import io.square1.richtextlib.v2.content.RichTextDocumentElement;

/**
 * Created by Omar on 15-Jul-18 10:10 PM.
 */
public class Extensions {

    @BindingAdapter("app:date")
    public static void formatDate(TextView view, Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMMM dd, yyyy", Locale.US);
        view.setText(formatter.format(date));
    }

    @BindingAdapter("app:html2")
    public static void setHtml(RichContentView contentView, String text) {
        RichTextDocumentElement element = RichTextV2.textFromHtml(contentView.getContext(), text);
        contentView.setText(element);
    }

    @BindingAdapter({"app:url", "app:placeholder"})
    public static void srcFromUrl(ImageView imageView, Uri uri, Drawable placeholder) {
        Glide.with(imageView).load(uri).apply(RequestOptions.placeholderOf(placeholder)).into(imageView);
    }
}
