package com.recalot.unittests;

import org.apache.commons.lang3.StringEscapeUtils;
import org.junit.Test;

import javax.xml.bind.DatatypeConverter;
import java.util.Date;

/**
 * @author Matthäus Schmedding (info@recalot.com)
 */
public class DummyTester {

    @Test
    public void dateTimeTest() {
        String xmlDateTimeString = "2015-08-10T13:37:24-07:00";
        Date date = DatatypeConverter.parseDateTime(xmlDateTimeString).getTime();

        System.out.println(date.getTime());


        Date modifiedDate = new Date(date.getTime() + 10);
        System.out.println(modifiedDate.getTime());


    }


    @Test
    public void testWordSplit() {
        String sentence = "Here&#39;s an idea. Open the articles and READ before you spread this nonsense. For instance, your very first link, the ICM Poll says this: They don&#39;t sympathize with the bombers, they sympathize with the &quot;feelings and motives&quot; which is an entirely different thing and could mean anything, from &quot;feelings&quot; of estrangement in society and &quot;motives&quot; of highlighting Western foreign policy in Iraq. 99% consider the terrorist attack WRONG.Your second link asserts that 25% of British Muslims think the bombing was justified, yet your first link says 99% are against it. So which one do we believe? Both are news stories and neither provide links to the actual study.Your third link is about the Iraq war and yes, plenty of people think that American aggression in that country was unjustified and the only legitimate way for them to defend themselves was suicide bombings. I don&#39;t support this, but we can hardly scrutinize people for not adhering to non-violence. Indeed, about 60% of Americans supported the initial attack on Iraq war and bought into the WMD lies which resulted in massive war crimes and the deaths of hundreds of thoudands of civilians and the creation of a vaccuum where millions of people were killed in sectarian violence and groups like ISIS were able to flourish.  Link 4 doesn&#39;t exist.Link 5 is the same as link 3, specially referring to American troops in Iraq. It&#39;s unfortunate that people think that violence is justfied but war is brutal and people generally support one side or the other. Again, nearly 60% of American civilians &quot;supported attack on Iraqi people&quot; before the Iraq invasion. If you refer to page 5 of the document though, you will find that the overwhelmingly large majorities in the Muslim world are against attacking U.S. civilians in general.link 5 and 6 is about Hezbollah and Hamas, again politics is involved and I support neither of those groups and consider them terrorists, but you have to realize that the Palestinian and Israeli issue is a polarizing one. If you look at some of the polls such as this where 44% of Americans think that Israeli actions against Palestinians are justified which would mean that they justify illegal settlements and bombings. Quite tragic.links 7 to 18 do not represent Muslim majorities and in fact the majority believes that violence is not justified according to those very polls. A bunch of them are not in english so I was not able to analyse it. 14, 15 and 16 don&#39;t exist.19 is problematic, and part of the reason why Palestine/Israel issue is not a simple one and it breaks my heart that people will give in to their hatred.The last one, 13% is still a minority who support these terrorist groups. Quite unfortunate, but a minority is a minority. Moreover, the complete finding of this particular poll is that Muslims garner little support for terrorists and are themselves concerned about religious extremism.I would urge you to consider these points before you copy and paste it to another thread. Most people do not care to read the documents and articles and would rather just read the title and make their judgement about Muslims. They will not consider the context and the political dynamics that generates such opinions, something which is essential to understanding a poll.";

        sentence = StringEscapeUtils.unescapeHtml4(sentence);
        String[] parts = sentence.split("(?<!^)\\b");

        String last = null;
        boolean concat = false;

        for (String word : parts) {
            if (last != null) {
                if (word.trim().length() == 0) {
                    System.out.println(last);
                    last = null;
                } else if ( (!concat && !word.equals("'") && last != null)) {
                    System.out.println(last);
                    last = word;
                } else if (word.equals("'")) {
                    concat = true;
                    last += word;
                } else {
                    if(concat) {
                        last += word;
                    } else {
                        last = word;
                    }
                    concat = false;
                }
            } else {
                last = word;
            }
        }

        if(last != null) {
            System.out.println(last);
        }

        System.out.println(parts.length);
    }
}
