TextWrapView
============

This is a collection of Android views to deal with text reflowing around an image and text ellipsizing (see screenshots below).

* **TextWrapView** is a view to display up to a specified amount of text lines, ellipsizing the input text so that the last word before the ellipsis will not be truncated in the middle.
* **ImageTextWrapView** provides an ImageView + TextView-like control which allows to show an image, a "title" at its right and a text flowing below it. The image size can be expressed in term of title+text lines or as an exact dimension. The bitmap is center-cropped by the **BitmapCropper** class. The text length is expressed in term of lines and it is ellipsized.
* **BitmapCropper** is an utility class which resizes a bitmap so that its lesser size equals the given size and then center-crops it.

![Screenshot vertical](http://dl.dropbox.com/u/1493094/textwrapview_v.png)

![Screenshot horizontal](http://dl.dropbox.com/u/1493094/textwrapview_h.png)
