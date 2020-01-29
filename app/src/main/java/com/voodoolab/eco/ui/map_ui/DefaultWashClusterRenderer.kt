package com.voodoolab.eco.ui.map_ui

import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.ui.IconGenerator
import com.voodoolab.eco.R


class DefaultWashClusterRenderer(
    context: Context?,
    map: GoogleMap?,
    clusterManager: ClusterManager<ClusterWash>?
) : DefaultClusterRenderer<ClusterWash>(context, map, clusterManager) {

    var iconGenerator: IconGenerator = IconGenerator(context)
    var clusterIconGenerator: IconGenerator = IconGenerator(context)

    var imageView: ImageView
    var clusterImageView: ImageView

    var clusterSizeTextView: TextView
    var itemTextView: TextView

    init {
        val clusterView =
            (context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                R.layout.cluster_layout,
                null
            )
        clusterImageView = clusterView.findViewById(R.id.icon)
        clusterSizeTextView = clusterView.findViewById(R.id.size)

        val itemView =
            (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                R.layout.cluster_layout,
                null
            )
        imageView = itemView.findViewById(R.id.icon)
        itemTextView = itemView.findViewById(R.id.size)
        val dp = context.resources.displayMetrics.density
        itemTextView.setPadding(0, 0, 0, (15 * dp).toInt())

        iconGenerator.setContentView(itemView)
        iconGenerator.setTextAppearance(R.style.HappyHoursTextStyle)

        clusterIconGenerator.setContentView(clusterView)
        clusterIconGenerator.setTextAppearance(R.style.HappyHoursTextStyle)

        clusterIconGenerator.setBackground(null)
        iconGenerator.setBackground(null)
    }

    override fun onBeforeClusterItemRendered(item: ClusterWash?, markerOptions: MarkerOptions?) {
        // render single item
        item?.icon?.let {
            imageView.setImageResource(it)
        }

        item?.active?.let {
            if (!it) {
                itemTextView.text = itemTextView.context.resources.getString(R.string.percent_value, item.cashback)
            }
        }
        val bitmapIcon = iconGenerator.makeIcon()
        markerOptions?.icon(BitmapDescriptorFactory.fromBitmap(bitmapIcon))
    }

    override fun onBeforeClusterRendered(
        cluster: Cluster<ClusterWash>?,
        markerOptions: MarkerOptions?
    ) {
        clusterImageView.setImageDrawable(
            clusterImageView.context.resources.getDrawable(
                R.drawable.ic_cluster,
                null
            )
        )
        clusterSizeTextView.text = cluster?.size.toString()
        val icon = clusterIconGenerator.makeIcon()
        markerOptions?.icon(BitmapDescriptorFactory.fromBitmap(icon))
    }

    override fun shouldRenderAsCluster(cluster: Cluster<ClusterWash>?): Boolean {
        return cluster?.size!! > 1
    }
}