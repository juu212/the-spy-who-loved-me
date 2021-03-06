package com.criteo.thespywholovedme.tokenizer;

import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;

public class TokenizerHelper {

	static Set<String> dictStopWords;
	static Set<String> dictSingularWords;
	static
	{
		String stopWords =
				"a,about,above,after,again,against,all,am,an,and,any,are,aren't,as,at,be,because,been,before,being,below,between,both,but,by,can't,cannot,could,couldn't,did,didn't,do,does,doesn't,doing,don't,down,during,each,few,for,from,further,had,hadn't,has,hasn't,have,haven't,having,he,he'd,he'll,he's,her,here,here's,hers,herself,him,himself,his,how,how's,i,i'd,i'll,i'm,i've,if,in,into,is,isn't,it,it's,its,itself,let's,me,more,most,mustn't,my,myself,no,nor,not,of,off,on,once,only,or,other,ought,our,oursourselves,out,over,own,same,shan't,she,she'd,she'll,she's,should,shouldn't,so,some,such,than,that,that's,the,their,theirs,them,themselves,then,there,there's,these,they,they'd,they'll,they're,they've,this,those,through,to,too,under,until,up,very,was,wasn't,we,we'd,we'll,we're,we've,were,weren't,what,what's,when,when's,where,where's,which,while,who,who's,whom,why,why's,with,won't,would,wouldn't,you,you'd,you'll,you're,you've,your,yours,yourself,yourselves,alors,au,aucuns,aussi,autre,avant,avec,avoir,bon,car,ce,cela,ces,ceux,chaque,ci,comme,comment,dans,des,du,dedans,dehors,depuis,devrait,doit,donc,dos,début,elle,elles,en,encore,essai,est,et,eu,fait,faites,fois,font,hors,ici,il,ils,je,juste,la,le,les,leur,là,ma,maintenant,mais,mes,mine,moins,mon,mot,même,ni,nommés,notre,nous,ou,où,par,parce,pas,peut,peu,plupart,pour,pourquoi,quand,que,quel,quelle,quelles,quels,qui,sa,sans,ses,seulement,si,sien,son,sont,sous,soyez,sujet,sur,ta,tandis,tellement,tels,tes,ton,tous,tout,trop,très,tu,voient,vont,votre,vous,vu,ça,étaient,état,étions,été,être";

		// add country/ state to stop words
		stopWords += ",united,states,england,france,canada,california,washington,virginia,ca,ny,va,wa,san,francisco,los,angeles";

		//add criteo
		stopWords += ",criteo";

		dictStopWords = new HashSet();
		String[] tks = stopWords.split(",");
		for (int i = 0; i < tks.length; i++) {
			dictStopWords.add(tks[i]);
		}

		String singularWords = "aws,iis, hdfs, js";
		dictSingularWords = new HashSet();
		tks = singularWords.split(",");
		for (int i = 0; i < tks.length; i++) {
			dictSingularWords.add(tks[i]);
		}
	}

	private static final PorterStemmer stemmer = new PorterStemmer();
	private static final Pattern normalizePattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
	private static int nMaxToken = 15;

	public final static Set<String> tokenize(String input) {
		if (input.isEmpty())
			return null;

		String normalizedInput = Normalizer.normalize(input,
				Normalizer.Form.NFD);
		//normalizedInput = normalizePattern.matcher(normalizedInput).replaceAll("");

		normalizedInput = input.toLowerCase().trim().replaceAll("[^0-9a-z#+\\s('s)]", " ").replaceAll("[()]", " ");
		String[] tks = normalizedInput.split("(\\s)+");

		Set<String> acceptedTokens = new HashSet<String>();
		for (int i=0; i<tks.length; i++) {
			// ignore empty string or single character token
			if (tks[i].length() < 2)
				continue;

			if (!tks[i].matches(".*[a-zA-Z]+.*"))
				continue;

			if (!dictStopWords.contains(tks[i])) {
				String token = tks[i];
				if (!dictSingularWords.contains(tks[i])) {
					stemmer.add(tks[i]);
					stemmer.stem();
					token = stemmer.toString();
					stemmer.reset();
				}
				acceptedTokens.add(token);
			}
		}

		return acceptedTokens;
	}
}
