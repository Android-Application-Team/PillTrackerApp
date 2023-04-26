package com.example.pilltracker


import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
//import com.bumptech.glide.Glide

private const val TAG = "DetailMedicineActivity"

class DetailMedicineActivity : AppCompatActivity() {
    private lateinit var mediaImageView: ImageView
    private lateinit var titleTextView: TextView
    private lateinit var priceTextView: TextView
    private lateinit var contentTextView: TextView
    private lateinit var companyNameTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.medication_details)

        // TODO: Find the views for the screen
        mediaImageView = findViewById(R.id.medImage)
        titleTextView = findViewById(R.id.medTitle)
        priceTextView = findViewById(R.id.medPrice)
        contentTextView = findViewById(R.id.medContent)
        companyNameTextView = findViewById(R.id.companyName)

//        // TODO: Get the extra from the Intent
//        //val article = intent.getSerializableExtra(ARTICLE_EXTRA) as DisplayArticle
//
//        // TODO: Set the title, byline, and abstract information from the article
//        titleTextView.text = article.headline
//        titleTextView.text = article.byline
//        abstractTextView.text = article.abstract
//
//        // TODO: Load the media image
//        Glide.with(this)
//            .load(article.mediaImageUrl)
//            .into(mediaImageView)
  }

}