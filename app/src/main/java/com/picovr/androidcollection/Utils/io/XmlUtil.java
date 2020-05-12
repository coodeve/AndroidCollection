package com.picovr.androidcollection.Utils.io;

import android.util.Xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * xml处理
 */
public class XmlUtil {

    private static final String CHARSET_UTF_8 = "UTF-8";

    public static abstract class AbsXmlParse<T> {

        public abstract void startDocument();

        public abstract void startTag(XmlPullParser xmlPullParser);

        public abstract void endTag(XmlPullParser xmlPullParser);

        public abstract void endDocument();

        public abstract String string();
    }

    public static abstract class AbsXmlSerializer<T> {

        public abstract void serialize(XmlSerializer xmlSerializer);
    }

    /**
     * 解析
     *
     * @param in
     * @param xmlParse
     * @return
     */
    public String parseXmlWithPull(InputStream in, AbsXmlParse xmlParse) {
        try {
            XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = xmlPullParserFactory.newPullParser();
            xmlPullParser.setInput(in, CHARSET_UTF_8);
            int eventType = xmlPullParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        xmlParse.startDocument();
                        break;
                    case XmlPullParser.START_TAG:
                        xmlParse.startTag(xmlPullParser);
                        break;
                    case XmlPullParser.END_TAG:
                        xmlParse.endTag(xmlPullParser);
                        break;
                }
                eventType = xmlPullParser.next();
            }
            xmlParse.endDocument();

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return xmlParse.string();
    }


    /**
     * 生成
     *
     * @param out
     * @param absXmlSerializer
     */
    public void generateXmlWithPull(OutputStream out, AbsXmlSerializer absXmlSerializer) {
        try {
            XmlSerializer xmlSerializer = Xml.newSerializer();
            xmlSerializer.setOutput(out, CHARSET_UTF_8);
            xmlSerializer.startDocument(CHARSET_UTF_8, true);
            absXmlSerializer.serialize(xmlSerializer);
            xmlSerializer.endDocument();
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Dom解析
     *
     * @param file
     */
    public static void parseXmlWithDom(File file, String tagName) {
        try {
            //DOM解析器的工厂示例:
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            //Dom工厂中获得dom解析器
            DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();
            //解析的xml文件读入Dom解析器
            Document document = dbBuilder.parse(file);
            NodeList elementsByTagNames = document.getElementsByTagName(tagName);
            for (int i = 0, len = elementsByTagNames.getLength(); i < len; i++) {
                Node item = elementsByTagNames.item(i);
                if (item.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) item;
                    String childTagName = element.getTagName();
                    // 还可以获取属性
                    NodeList childNodes = element.getChildNodes();
                    for (int j = 0, jlen = childNodes.getLength(); j < jlen; j++) {
                        if (item.getNodeType() == Node.ELEMENT_NODE) {
                            Element childElement = (Element) item;
                            String child2TagName = element.getTagName();
                            // 还可以获取属性
                        }
                    }
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * SAX解析xml
     *
     * @param file
     */
    public static void parseXmlWithSAX(File file) {
        //①创建XML解析处理器
        SaxHelper ss = new SaxHelper();
        //②得到SAX解析工厂
        SAXParserFactory factory = SAXParserFactory.newInstance();
        //③创建SAX解析器
        SAXParser parser = null;
        try {
            parser = factory.newSAXParser();
            //④将xml解析处理器分配给解析器,对文档进行解析,将事件发送给处理器
            parser.parse(file, ss);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class SaxHelper extends DefaultHandler {
        public SaxHelper() {
            super();
        }

        @Override
        public void startDocument() throws SAXException {
            super.startDocument();
        }

        @Override
        public void endDocument() throws SAXException {
            super.endDocument();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);
        }
    }
}
