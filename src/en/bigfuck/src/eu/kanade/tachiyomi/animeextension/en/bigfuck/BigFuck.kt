package eu.kanade.tachiyomi.animeextension.en.bigfuck

import eu.kanade.tachiyomi.animesource.model.AnimeFilterList
import eu.kanade.tachiyomi.animesource.model.SAnime
import eu.kanade.tachiyomi.animesource.model.SEpisode
import eu.kanade.tachiyomi.animesource.model.Video
import eu.kanade.tachiyomi.animesource.online.ParsedAnimeHttpSource
import okhttp3.Request
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class BigFuck : ParsedAnimeHttpSource() {

    override val name = "BigFuck"

    override val baseUrl = "https://bigfuck.com"

    override val lang = "en"

    override val supportsLatest = true

    override fun popularAnimeRequest(page: Int): Request = Request.Builder().url("$baseUrl/popular/$page").build()
    override fun popularAnimeSelector(): String = "div.video"
    override fun popularAnimeFromElement(element: Element): SAnime = SAnime.create().apply {
        setUrlWithoutDomain(element.select("a").attr("href"))
        title = element.select("span.title").text()
        thumbnail_url = element.select("img").attr("src")
    }
    override fun popularAnimeNextPageSelector(): String? = "a.next"

    override fun latestUpdatesRequest(page: Int): Request = Request.Builder().url("$baseUrl/latest/$page").build()
    override fun latestUpdatesSelector(): String = popularAnimeSelector()
    override fun latestUpdatesFromElement(element: Element): SAnime = popularAnimeFromElement(element)
    override fun latestUpdatesNextPageSelector(): String? = popularAnimeNextPageSelector()

    override fun searchAnimeRequest(page: Int, query: String, filters: AnimeFilterList): Request = Request.Builder().url("$baseUrl/search/$query/$page").build()
    override fun searchAnimeSelector(): String = popularAnimeSelector()
    override fun searchAnimeFromElement(element: Element): SAnime = popularAnimeFromElement(element)
    override fun searchAnimeNextPageSelector(): String? = popularAnimeNextPageSelector()

    override fun animeDetailsParse(document: Document): SAnime = SAnime.create().apply {
        title = document.select("h1").text()
    }

    override fun episodeListSelector(): String = "ul.episodes"
    override fun episodeFromElement(element: Element): SEpisode = SEpisode.create().apply {
        name = "Episode 1"
        setUrlWithoutDomain(element.select("a").attr("href"))
    }

    override fun videoListSelector(): String = "div.video-player"
    override fun videoFromElement(element: Element): Video = throw Exception("Not implemented")
    override fun videoUrlParse(document: Document): String = throw Exception("Not implemented")
}
