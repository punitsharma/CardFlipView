# CardFlipView

CardFlipView with Animation of Flipping from left to right and top to bottom (Vice versa)

![](https://github.com/punitsharma/CardFlipView/blob/develop/gif/video.gif)

You can use this as a library dependecy in your project 

1. You need to create a CardFlipView like below 

     
        <CardFlipView xmlns:android="http://schemas.android.com/apk/res/android"
          xmlns:app="http://schemas.android.com/apk/res-auto"
          android:id="@+id/cardflipview"
          android:layout_width="match_parent"
          android:layout_height="wrap_content"
          app:cardFlipDuration="400"
          app:cardFlipEnabled="true"
          app:cardFlipOnTouch="false">
    
          <!-- Back Layout Goes Here -->
          <include layout="@layout/your_back_layout" />

          <!-- Front Layout Goes Here -->
          <include layout="@layout/your_front_layout" />

         </CardFlipView>


2. For flippling the card you have to call

         cardflipview.flipCard() 
     
