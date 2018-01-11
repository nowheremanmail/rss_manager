package com.dag.news.feeds;

import java.io.IOException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;

import org.apache.http.Header;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dag.news.bo.TempFeed;
import com.dag.news.bo.TempNew;
import com.dag.news.model.Feed;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

//import com.sun.syndication.feed.synd.SyndFeed;
//import com.sun.syndication.io.FeedException;
//import com.sun.syndication.io.SyndFeedInput;
//import com.sun.syndication.io.XmlReader;

@Component
public class BasicRssReaderImpl implements BasicRssReader {
	static final String TITLE = "title";
	static final String DESCRIPTION = "description";
	static final String CHANNEL = "channel";
	static final String LANGUAGE = "language";
	static final String COPYRIGHT = "copyright";
	static final String LINK = "link";
	static final String AUTHOR = "author";
	static final String ITEM = "item";
	static final String UPDATED = "updated";
	static final String PUB_DATE = "pubDate";
	static final String LAST_PUB_DATE = "lastBuildDate";
	static final String GUID = "guid";
	static final String TTL = "ttl";

	@Value("${net.timeout:20000}")
	int timeout = 20000;

	static private Logger logger = LoggerFactory.getLogger(BasicRssReaderImpl.class);

	static TrustStrategy trustStrategy = new TrustStrategy() {

		public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			if (logger.isDebugEnabled()) {
				for (X509Certificate cert : chain) {
					logger.debug("certification " + cert);
				}
			}
			return true;
		}

	};

	@Override
	public SyndFeed readFeed(Feed _feed) throws Exception {
		SyndFeed feed = null;
		CloseableHttpClient httpclient = null;

		try {
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, trustStrategy).build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());

			httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();

			HttpGet httpget = new HttpGet(_feed.getUrl());
			
			httpget.setHeader("Accept","text/html, application/xhtml+xml, image/jxr, */*");
			httpget.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36 Edge/15.15063");

			httpget.setHeader("Accept-Encoding","gzip, deflate");
			httpget.setHeader("Accept-Language","en-GB, en; q=0.8, es-ES; q=0.6, es; q=0.4, ca; q=0.2");
			
			httpget.setConfig(RequestConfig.custom().setSocketTimeout(timeout).setConnectionRequestTimeout(timeout)
					.setConnectTimeout(timeout).setCircularRedirectsAllowed(true).setRedirectsEnabled(true).build());

			TimerTask task = new TimerTask() {
				@Override
				public void run() {
					try {
						if (httpget != null) {
							httpget.abort();
						}
					} catch (Exception ex) {

					}
				}
			};
			Timer time = new Timer(true);
			time.schedule(task, timeout);

			logger.debug("Executing request " + httpget.getRequestLine());

			CloseableHttpResponse response = httpclient.execute(httpget);
			time.cancel();
			Header _contentType = null;
			try {
				_contentType = response.getFirstHeader("Content-Type");

if (logger.isDebugEnabled())
				logger.debug("get " + response.getStatusLine() + " " + _contentType);

				if (response.getStatusLine().getStatusCode() == 200) {
					SyndFeedInput input = new SyndFeedInput();
					feed = input.build(new XmlReader(response.getEntity().getContent()));
				} else {
					throw new RuntimeException("network error [" + response.getStatusLine().getStatusCode() + "]");
				}
			} finally {
				response.close();
			}
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {

			}
		}

		return feed;
	}

	public TempFeed read(Feed _feed) {
		TempFeed result = new TempFeed(_feed);
		CloseableHttpClient httpclient = null;

		try {
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, trustStrategy).build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());

			httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();

			boolean isFeedHeader = true;
			// Set header values intial to the empty string
			String description = "";
			String title = "";
			String link = "";
			String language = "";
			String copyright = "";
			String author = "";
			String pubdate = "", updated = "";
			String lastpubdate = "";
			String guid = "";
			String ttl = "";
			// First create a new XMLInputFactory
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			// Setup a new eventReader
			inputFactory.setProperty("javax.xml.stream.isReplacingEntityReferences", true);

			HttpGet httpget = new HttpGet(_feed.getUrl());
			httpget.setConfig(RequestConfig.custom().setSocketTimeout(5000).setConnectionRequestTimeout(5000)
					.setConnectTimeout(5000).setCircularRedirectsAllowed(true).setRedirectsEnabled(true).build());

			// System.out.println("Executing request " +
			// httpget.getRequestLine());

			logger.debug("Executing request " + httpget.getRequestLine());
			CloseableHttpResponse response = httpclient.execute(httpget);

			try {
				if (response.getStatusLine().getStatusCode() == 200) {

					// InputStream in = url.openStream();
					XMLEventReader eventReader = inputFactory.createXMLEventReader(response.getEntity().getContent());
					// read the XML document
					while (eventReader.hasNext()) {
						XMLEvent event = eventReader.nextEvent();
						if (event.isStartElement()) {
							String localPart = event.asStartElement().getName().getLocalPart();
							switch (localPart) {
							case ITEM:
								if (isFeedHeader) {
									isFeedHeader = false;
									result.setTitle(title);
									result.setDescription(description);

									if (pubdate != null && pubdate.trim().length() > 0) {
										result.setPubDate(pubdate.trim());
									} else if (lastpubdate != null) {
										result.setPubDate(lastpubdate.trim());
									} else if (updated != null) {
										result.setPubDate(updated);
									}

									/*
									 * feed.setNextUpdate(convertDate(pubdate));
									 * // we set // nextDate // to // published
									 * // date // to be // able // to //
									 * calculate // right // refresh // time
									 */

									result.setTtl(ttl);
									result.setLanguage(language);
									// if (feed.getLanguage() == null ||
									// !feed.getLanguage().getName().equals(language))
									// {
									// logger.info("changing language for " +
									// feed.toString());
									// feed.setLanguage(languageService.getOrInsert(language));
									// }
									// if (ttl.length() > 0) {
									// try {
									// feed.setTtl(Integer.parseInt(ttl));
									// } catch (NumberFormatException ex) {
									// logger.info("invalid ttl <" + ttl + ">
									// for "
									// +
									// feed.toString());
									// }
									// } else {
									// logger.info("no ttl for " +
									// feed.toString());
									// }
									if (_feed.getLastUpdate() != null
											&& _feed.getLastUpdate().equals(result.getPubDate())) {
										return result;
									}

								}
								event = eventReader.nextEvent();
								break;
							case UPDATED:
								updated = getCharacterData(event, eventReader);
								break;
							case TITLE:
								title = getCharacterData(event, eventReader);
								break;
							case TTL:
								ttl = getCharacterData(event, eventReader);
								break;
							case DESCRIPTION:
								description = getCharacterData(event, eventReader);
								break;
							case LINK:
								link = getCharacterData(event, eventReader);
								break;
							case GUID:
								guid = getCharacterData(event, eventReader);
								break;
							case LANGUAGE:
								language = getCharacterData(event, eventReader);
								break;
							case AUTHOR:
								author = getCharacterData(event, eventReader);
								break;
							case PUB_DATE:
								pubdate = getCharacterData(event, eventReader);
								break;
							case LAST_PUB_DATE:
								lastpubdate = getCharacterData(event, eventReader);
								break;
							case COPYRIGHT:
								copyright = getCharacterData(event, eventReader);
								break;
							}
						} else if (event.isEndElement()) {
							if (event.asEndElement().getName().getLocalPart() == (ITEM)) {
								
								link = extractGoogleLink(link);
								
								TempNew tmpNew = new TempNew(title, description, link, pubdate, language);

								result.addNew(tmpNew);

								event = eventReader.nextEvent();
								continue;
							}
						}
					}
				} else {
					result.setError(null);
					result.setMessage(String.format(Locale.US, "%1$tF %1$tT > %2$s", Calendar.getInstance().getTime(),
							response.getStatusLine()));
					logger.error("error HTML processing " + _feed + " " + response.getStatusLine());
					return result;
				}
			} finally {
				response.close();
			}
		} catch (Exception ex) {
			result.setError(ex);
			result.setMessage(
					String.format(Locale.US, "%1$tF %1$tT > %2$s", Calendar.getInstance().getTime(), ex.getMessage()));
			logger.error("error processing " + _feed, ex);
			return result;
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}
		}

		return result;
	}

	static public String extractGoogleLink(String url) {
		if (url.startsWith("http://news.google") || url.startsWith("https://news.google")) {

			Pattern pattern = Pattern.compile("(&?url=)([^&]+)");
			Matcher matcher = pattern.matcher(url);
			// check all occurance
			if (matcher.find() && matcher.groupCount() == 2) {
				url = matcher.group(2);
			}
		}
		return url;
	}
	
	private String getCharacterData(XMLEvent event, XMLEventReader eventReader) throws XMLStreamException {
		String result = "";
		event = eventReader.nextEvent();

		while (true) {
			if (event instanceof Characters) {
				result += event.asCharacters().getData();
			}
			if (eventReader.peek() instanceof Characters) {
				event = eventReader.nextEvent();
			} else {
				break;
			}
		}

		return result;
	}

	public static void main(String[] a) throws Exception {
		String u = "http://ineverycrea.net/comunidad/ineverycrea/mi-perfil/inicio";
		u = "http://www.larazon.es/rss/internacional.xml";
		u = "http://rss.elconfidencial.com/espana/";
		u = "http://estaticos.expansion.com/rss/portada.xml";
		u = "http://newsrss.bbc.co.uk/rss/on_this_day/front_page/rss.xml";
		u = "http://estaticos.elmundo.es/elmundo/rss/ciencia.xml";
		u = "http://www.bloglines.com/";
		u = "http://news.google.gr/?output=rss";
		u="http://www.bbc.com/persian/index.xml";
		// u= "https://tienda.abc.es/";
		// u = "http://thescotsman.scotsman.com/index.cfm?format=rss";
		// u="http://www.20minutos.es/rss/";
		// u="http://www.larazon.es/rss/local/madrid.xml";
		// u="http://elpais.com/tag/rss/cine/a/";
		// URL feedUrl = new URL(args[0]);
		//

		// http://news.google.com/news/url?sa=t&fd=R&ct2=el_gr&usg=AFQjCNGaR8lUoAnnzj7gfkcVOdmwqm3Dbw&clid=c3a7d30bb8a4878e06b80cf16b898331&cid=52779995241293&ei=O3hSV_D1EsavzAae7ryIAw&url=http://www.zougla.gr/kosmos/article/meksiko-vre8ike-mazikos-tafos-me-117-nekrous
		try {
			BasicRssReaderImpl kk = new BasicRssReaderImpl();
			SyndFeed feedx = kk.readFeed(new Feed(u));

			System.out.println(feedx.getPublishedDate().toGMTString());
			System.out.println(feedx.getTitle());
			for (SyndEntry s : feedx.getEntries()) {

				System.out.println(s.getTitle());

				String url = s.getLink();

				System.out.println(extractGoogleLink(url));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		// // URL url = new URL(u);
		// // InputStream in = url.openStream();
		// // in.read(new byte[1024], 0, 1024);
		//
		// // HttpGet get = new HttpGet(u);
		// SyndFeed feed = null;
		// CloseableHttpClient httpclient = null;
		// try {
		// HttpGet httpget = new HttpGet(u);
		// httpget.setConfig(RequestConfig.custom().setSocketTimeout(5000).setConnectionRequestTimeout(5000)
		// .setConnectTimeout(5000).setCircularRedirectsAllowed(true).build());
		//
		// SSLContext sslContext = new
		// SSLContextBuilder().loadTrustMaterial(null, trustStrategy).build();
		// SSLConnectionSocketFactory sslsf = new
		// SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());
		//
		// httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
		//
		// System.out.println("Executing request " + httpget.getRequestLine());
		// CloseableHttpResponse response = httpclient.execute(httpget);
		// try {
		//
		// System.out.println("----------------------------------------");
		// System.out.println(response.getStatusLine());
		// //System.out.println(EntityUtils.toString(response.getEntity()));
		//
		//
		// //response.getHeaders("Content-type")
		//
		//
		// SyndFeedInput input = new SyndFeedInput();
		// feed = input.build(new XmlReader(response.getEntity().getContent()));
		//
		//// feed = input.build(new XmlReader(new URL(u)));
		//
		// } finally {
		// response.close();
		// }
		// } catch (Exception ex) {
		// ex.printStackTrace();
		// } finally {
		// httpclient.close();
		// }
		// SyndFeedInput input = new SyndFeedInput();
		//
		// feed = input.build(new XmlReader(new URL(u)));
		// System.out.println(feed);
	}
}
