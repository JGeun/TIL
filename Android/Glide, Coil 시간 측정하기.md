### Glide

```kotlin
val glideStartTime = System.currentTimeMillis()

Glide.with(this)
    .load(imageUrl)
    .listener(object : RequestListener<Drawable> {
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>?,
            isFirstResource: Boolean,
        ): Boolean {
            return false
        }

        override fun onResourceReady(
            resource: Drawable?,
            model: Any?,
            target: Target<Drawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean,
        ): Boolean {
            val glideEndTime = System.currentTimeMillis()
            Log.d("ImageProcessTime", "$funcName processTime: ${(glideEndTime - glideStartTime) / 1000}")
//           Log.d("check@@@", "${resource} ${model} ${target} ${dataSource} ${isFirstResource}")
            return false
        }
    })
    .into(imageView)
```

### coil

```kotlin
Log.d("ImageProcessTime", "$funcName Coil 시작")
val coilStartTime = System.currentTimeMillis()

imageView.load(imageUrl) {
    crossfade(true)
    listener { request, result ->
        val coilEndTime = System.currentTimeMillis()
        Log.d("ImageProcessTime", "$funcName processTime: ${(coilEndTime - coilStartTime) / 1000}")
    }
}
```