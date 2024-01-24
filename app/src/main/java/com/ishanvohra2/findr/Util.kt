package com.ishanvohra2.findr

import com.ishanvohra2.findr.data.EventResponseItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

fun <T1, T2, R> combineState(
    flow1: StateFlow<T1>,
    flow2: StateFlow<T2>,
    scope: CoroutineScope,
    sharingStarted: SharingStarted = SharingStarted.Eagerly,
    transform: (T1, T2) -> R
): StateFlow<R> = combine(flow1, flow2) {
        o1, o2 -> transform.invoke(o1, o2)
}.stateIn(scope, sharingStarted, transform.invoke(flow1.value, flow2.value))

fun getTextFromEvent(event: EventResponseItem): String {
    return when(event.type){
        "CommitCommentEvent" -> {
            val action = event.payload["action"] as String
            return "$action a comment on a commit in this repository"
        }
        "CreateEvent" -> {
            return when (event.payload["ref_type"] as String) {
                "branch" -> {
                    "created a branch in this repository"
                }
                "tag" -> {
                    "created a tag in this repository"
                }
                else -> {
                    "created this repository"
                }
            }
        }
        "DeleteEvent" -> {
            return when (event.payload["ref_type"] as String) {
                "branch" -> {
                    "deleted a branch"
                }
                "tag" -> {
                    "deleted a tag"
                }
                else -> {
                    "deleted something from this repository"
                }
            }
        }
        "ForkEvent" -> {
            return "forked this repository"
        }
        "GollumEvent" -> {
            return "added/updated wiki pages for this repository"
        }
        "IssueCommentEvent" -> {
            val action = (event.payload)["action"] as String
            return "$action a comment on an issue."
        }
        "IssuesEvent" -> {
            return when (val action = event.payload["action"] as String) {
                "opened", "edited", "closed", "reopened" -> {
                    "$action an issue was in this repository"
                }
                "assigned" -> {
                    "assigned an issue in this repository"
                }
                "unassigned" -> {
                    "unassigned an issue in this repository"
                }
                "labeled" -> {
                    "added a label to an issue in this repository"
                }
                "unlabeled" -> {
                    "removed a label to an issue in this repository"
                }
                else -> {
                    "Something was done to an issue."
                }
            }
        }
        "MemberEvent" -> {
            val action = event.payload["action"] as String
            return if (action == "added") {
                "added a user in this repository"
            } else {
                "updated the members in this repository"
            }
        }
        "PublicEvent" -> {
            return "made this repository public"
        }
        "PullRequestReviewEvent" -> {
            val action = event.payload["action"] as String
            val number = (event.payload["pull_request"] as Map<*, *>)["number"]
                    as Double
            return if (action == "created") {
                "created a review on the pull request !${number.toInt()} in this repository"
            } else {
                "updated the pull request !${number.toInt()} in this repository"
            }
        }
        "PullRequestReviewCommentEvent" -> {
            val action = event.payload["action"] as String
            val number = ((event.payload["pull_request"] as Map<*, *>)["number"]
                    as Double).toInt()
            return if (action == "created") {
                "added a comment on the pull request !$number in this repository"
            } else {
                "updated a comment on the pull request !$number in this repository"
            }
        }
        "PullRequestReviewThreadEvent" -> {
            val action = event.payload["action"] as String
            val number = ((event.payload["pull_request"] as Map<*, *>)["number"]
                    as Double).toInt()
            return when(action){
                "resolved" -> {
                    "resolved a thread in the pull request !$number in this repository"
                }
                "unresolved" -> {
                    "marked a thread as unresolved in the pull request !$number in this repository"
                }
                else -> "updated a thread in the pull request !$number in this repository"
            }
        }
        "PushEvent" -> {
            return "pushed code to this repository"
        }
        "ReleaseEvent" -> {
            val action = (event.payload as Map<*, *>)["action"] as String
            "$action a release in this repository"
        }
        "WatchEvent" -> {
            val action = event.payload["action"] as String
            return if (action == "started") {
                "starred this repository"
            } else {
                "unstarred this repository"
            }
        }
        "SponsorshipEvent" -> {
            val action = event.payload["action"] as String
            return when (action) {
                "created" -> {
                    "created a sponsorship in this repository"
                }
                "cancelled" -> {
                    "cancelled the sponsorship in this repository"
                }
                "pending_cancelled" -> {
                    "marked a sponsorship cancellation as pending"
                }
                "tier_changed" -> {
                    "changed the tier of sponsorship in this repository"
                }
                else -> {
                    "updated the sponsorship in this repository"
                }
            }
        }
        else -> ""
    }
}