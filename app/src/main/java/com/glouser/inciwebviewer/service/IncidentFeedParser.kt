package com.glouser.inciwebviewer.service

import android.util.Log
import android.util.Xml
import com.glouser.inciwebviewer.database.Incident
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.util.*

/**
 * Parser for InciWeb incident feed RSS documents.
 */
internal class IncidentFeedParser {

    /**
     * Parse RSS containing incidents from the given InputStream.
     *
     * @param inputStream an InputStream
     * @return the list of incidents from the RSS
     * @throws IOException            if there is a problem reading the InputStream
     * @throws XmlPullParserException if there is an XML error
     */
    @Throws(IOException::class, XmlPullParserException::class)
    fun parse(inputStream: InputStream): List<Incident> {
        inputStream.use {
            val parser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(it, null)
            return readFeed(parser)
        }
    }

    /**
     * Get incidents from the RSS feed.
     *
     * @param parser an XML parser that is parsing an RSS feed
     * @return the list of incidents
     * @throws IOException            if the XML parser has a problem reading the input
     * @throws XmlPullParserException if the XML is formatted or structured incorrectly
     */
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readFeed(parser: XmlPullParser): List<Incident> {
        val entries = ArrayList<Incident>()

        // The document should start with an <rss> tag containing a <channel>.
        // Example:
        // <rss xmlns="http://www.w3.org/2005/Atom">
        //   <channel>
        //     ...
        //   </channel>
        // </rss>
        parser.nextTag()
        parser.require(XmlPullParser.START_TAG, NS, "rss")
        parser.nextTag()
        parser.require(XmlPullParser.START_TAG, NS, "channel")
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            // Each <item> represents one incident.
            val name = parser.name
            if (name == "item") {
                entries.add(readItem(parser))
            } else {
                skip(parser)
            }
        }
        Log.v(TAG, "finished parsing, size: " + entries.size)
        return entries
    }

    /**
     * Read one item element.
     *
     * @param parser an XML parser that is parsing an RSS feed
     * @return an incident represent the item
     * @throws IOException            if the XML parser has a problem reading the input
     * @throws XmlPullParserException if the XML is formatted or structured incorrectly
     */
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readItem(parser: XmlPullParser): Incident {
        // Example item:
        // <item>
        //   <title>Snow Creek Fire (Wildfire)</title>
        //   <published>Thu, 15 Aug 2019 17:11:16 -05:00</published>
        //   <pubDate>Thu, 15 Aug 2019 17:11:16 -05:00</pubDate>
        //   <georss:point>47.703333333333 -113.4</georss:point>
        //   <geo:lat>47.703333333333</geo:lat>
        //   <geo:long>-113.4</geo:long>
        //   <link>http://inciweb.nwcg.gov/incident/6497/</link>
        //   <guid isPermaLink="true">http://inciweb.nwcg.gov/incident/6497/</guid>
        //   <description>
        //      Although still creeping and smoldering, minimal fire activity ...
        //   </description>
        //</item>
        val incident = Incident()

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            when (parser.name) {
                "title" -> {
                    incident.name = readText(parser)
                    parser.require(XmlPullParser.END_TAG, NS, "title")
                }
                "published" -> {
                    incident.date = readText(parser)
                    parser.require(XmlPullParser.END_TAG, NS, "published")
                }
                "description" -> {
                    incident.description = readText(parser)
                    parser.require(XmlPullParser.END_TAG, NS, "description")
                }
                "link" -> {
                    incident.link = readText(parser)
                    parser.require(XmlPullParser.END_TAG, NS, "link")
                }
                "geo:lat" -> {
                    incident.latitude = readText(parser)
                    parser.require(XmlPullParser.END_TAG, NS, "geo:lat")
                }
                "geo:long" -> {
                    incident.longitude = readText(parser)
                    parser.require(XmlPullParser.END_TAG, NS, "geo:long")
                }
                else -> {
                    Log.v(TAG, "skipping " + parser.name)
                    skip(parser)
                }
            }
        }

        return incident
    }

    /**
     * Get the contents of a text node.
     */
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readText(parser: XmlPullParser): String {
        var result: String? = null
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.text.trim()
            parser.nextTag()
        }
        return result ?: ""
    }

    /**
     * Skips tags the parser isn't interested in. Uses depth to handle nested tags. i.e.,
     * if the next tag after a START_TAG isn't a matching END_TAG, it keeps going until it
     * finds the matching END_TAG (as indicated by the value of "depth" being 0).
     */
    @Throws(XmlPullParserException::class, IOException::class)
    private fun skip(parser: XmlPullParser) {
        check(parser.eventType == XmlPullParser.START_TAG)
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }

    companion object {

        // Tag for logging.
        private val TAG = IncidentFeedParser::class.java.simpleName

        // Don't use XML namespaces.
        private val NS: String? = null
    }
}
