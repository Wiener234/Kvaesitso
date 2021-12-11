package de.mm20.launcher2.ui.legacy.search

import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.transition.Scene
import de.mm20.launcher2.badges.BadgeProvider
import de.mm20.launcher2.icons.IconRepository
import de.mm20.launcher2.ktx.dp
import de.mm20.launcher2.ktx.lifecycleScope
import de.mm20.launcher2.legacy.helper.ActivityStarter
import de.mm20.launcher2.search.data.Searchable
import de.mm20.launcher2.ui.R
import de.mm20.launcher2.ui.legacy.searchable.SearchableView
import de.mm20.launcher2.ui.legacy.view.LauncherIconView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class BasicGridRepresentation : Representation, KoinComponent {

    private val iconRepository: IconRepository by inject()
    private val badgeProvider: BadgeProvider by inject()

    override fun getScene(
        rootView: SearchableView,
        searchable: Searchable,
        previousRepresentation: Int?
    ): Scene {
        val context = rootView.context as AppCompatActivity
        val scene = Scene.getSceneForLayout(rootView, R.layout.view_basic_grid, rootView.context)
        scene.setEnterAction {
            with(rootView) {
                val text = findViewById<TextView>(R.id.label)
                text.text = searchable.label
                /*text.alpha = 0f
                text.animate()
                        .setStartDelay(300)
                        .setDuration(200)
                        .alpha(1f)
                        .start()*/
                findViewById<LauncherIconView>(R.id.icon).apply {
                    badge = badgeProvider.getLiveBadge(searchable.badgeKey)
                    shape = LauncherIconView.getDefaultShape(context)
                    setOnClickListener {
                        if (!ActivityStarter.start(
                                context,
                                rootView.findViewById(R.id.card),
                                item = searchable
                            )
                        ) {
                            rootView.representation = SearchableView.REPRESENTATION_FULL
                        }
                    }
                    icon = iconRepository.getIconIfCached(searchable)
                    lifecycleScope.launch {
                        iconRepository.getIcon(searchable, (84 * rootView.dp).toInt()).collectLatest {
                            icon = it
                        }
                    }
                    setOnLongClickListener {
                        rootView.representation = SearchableView.REPRESENTATION_FULL
                        true
                    }
                }
            }
        }

        return scene

    }

}
