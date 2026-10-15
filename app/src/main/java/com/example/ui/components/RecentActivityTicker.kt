package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberGold
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.SlateCardElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

private val RECENT_FEED = listOf(
    "🎉 Jessica R. just withdrew $100.00 via PayPal • 2m ago",
    "⚡ Marcus T. scored 1,000 pts on Lucky Wheel • 4m ago",
    "💸 David K. just withdrew $100.00 via Venmo • 8m ago",
    "🔥 Sophia B. reached the $75 Milestone • 11m ago",
    "💰 Chloe W. cashed out $100.00 via Direct Bank • 14m ago",
    "💎 Anthony L. matched 3 Jackpots on Golden Scratcher • 18m ago"
)

@Composable
fun RecentActivityTicker(
    modifier: Modifier = Modifier
) {
    var currentIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(3800)
            currentIndex = (currentIndex + 1) % RECENT_FEED.size
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(SlateCardElevated)
            .padding(horizontal = 12.dp, vertical = 7.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(EmeraldPrimary)
            )
            Spacer(modifier = Modifier.width(8.dp))
            AnimatedContent(
                targetState = RECENT_FEED[currentIndex],
                transitionSpec = {
                    (slideInVertically { height -> height } + fadeIn()) togetherWith
                            (slideOutVertically { height -> -height } + fadeOut())
                },
                label = "tickerAnimation"
            ) { text ->
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    maxLines = 1
                )
            }
        }
    }
}
