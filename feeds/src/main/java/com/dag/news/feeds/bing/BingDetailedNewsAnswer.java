package com.dag.news.feeds.bing;

import java.util.Date;

public class BingDetailedNewsAnswer {
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDatePublished() {
		return datePublished;
	}
	public void setDatePublished(String datePublished) {
		this.datePublished = datePublished;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	String name;
	String url;
	String description;
    String datePublished;
    String category;
	@Override
	public String toString() {
		return "BingDetailedNewsAnswer [name=" + name + ", url=" + url + ", description=" + description
				+ ", datePublished=" + datePublished + ", category=" + category + "]";
	}
	
/**
 	    {
	      "name": "Far-Right Politicians Rush To Take Advantage Of The Berlin Attack",
	      "url": "http://www.bing.com/cr?IG=9036717B9C984A7CABD52A1D79125D5A&CID=11DDC89CCCDA675D3B05C16ECDEB66B1&rd=1&h=f9lDe631TLbJm5C9UoDPXYpYb7MAQBY_fI38JR39WWQ&v=1&r=http%3a%2f%2fwww.huffingtonpost.com%2fentry%2fberlin-turkey-attack-trump_us_58594175e4b0b3ddfd8ea393&p=DevEx,5016.1",
	      "image": {
	        "thumbnail": {
	          "contentUrl": "https://www.bing.com/th?id=ON.AC51B7EAA9C60EB52F7CA01FCF717BFA&pid=News",
	          "width": 630,
	          "height": 397
	        }
	      },
	      "description": "In the space of a few hours on Monday, Russia’s ambassador to Turkey was assassinated in Ankara and a dozen people were killed when someone drove a truck into a Christmas market in Berlin. But while law enforcement officials tried to gather as much ...",
	      "about": [
	        {
	          "readLink": "https://api.cognitive.microsoft.com/api/v5/entities/42784943-7c23-7672-5527-06f89b965cdf",
	          "name": "Berlin"
	        },
	        {
	          "readLink": "https://api.cognitive.microsoft.com/api/v5/entities/be62642a-90a4-5740-65b6-19ad03ff0773",
	          "name": "Rush"
	        },
	        {
	          "readLink": "https://api.cognitive.microsoft.com/api/v5/entities/05dae247-32dc-5b43-8700-d90cade7b60a",
	          "name": "Attack"
	        }
	      ],
	      "provider": [
	        {
	          "_type": "Organization",
	          "name": "The Huffington Post"
	        }
	      ],
	      "datePublished": "2016-12-21T22:34:00",
	      "category": "World",
	      "headline": true
	    },
	    {
	      "name": "There&#39;s a clear Democratic front-runner for 2020",
	      "url": "http://www.bing.com/cr?IG=9036717B9C984A7CABD52A1D79125D5A&CID=11DDC89CCCDA675D3B05C16ECDEB66B1&rd=1&h=G4OJ_-RIKzHP_RYYPY4DBD1u1gHGSYm4NHfb2kn20Bg&v=1&r=http%3a%2f%2fwww.usatoday.com%2fstory%2fnews%2fpolitics%2f2016%2f12%2f21%2fdemocrats-running-for-president-2020%2f95651728%2f&p=DevEx,5018.1",
	      "image": {
	        "thumbnail": {
	          "contentUrl": "https://www.bing.com/th?id=ON.CCC651F2B48B721456D0AFE6E7A0F429&pid=News",
	          "width": 534,
	          "height": 401
	        }
	      },
	      "description": "WASHINGTON — On the theory that it&#39;s never too early to launch the next campaign, the new USA TODAY/Suffolk University Poll has identified an overwhelming front-runner for the Democratic presidential nomination in 2020. It&#39;s someone entirely new.",
	      "about": [
	        {
	          "readLink": "https://api.cognitive.microsoft.com/api/v5/entities/a6184b58-ce0a-3013-566e-e8404adb9aa5",
	          "name": "Front-runner"
	        },
	        {
	          "readLink": "https://api.cognitive.microsoft.com/api/v5/entities/022d3e13-c9e7-91f5-ce61-8a3d48d58fc6",
	          "name": "Democratic Party"
	        }
	      ],
	      "mentions": [
	        {
	          "name": "Front-runner"
	        },
	        {
	          "name": "Democratic Party"
	        },
	        {
	          "name": "United States"
	        }
	      ],
	      "provider": [
	        {
	          "_type": "Organization",
	          "name": "USA Today"
	        }
	      ],
	      "datePublished": "2016-12-21T22:14:00",
	      "category": "Politics",
	      "headline": true
	    },
	    {
	      "name": "U.S. Denies Russian Claim Dialogue Between Two Nations &#39;Frozen&#39;",
	      "url": "http://www.bing.com/cr?IG=9036717B9C984A7CABD52A1D79125D5A&CID=11DDC89CCCDA675D3B05C16ECDEB66B1&rd=1&h=2XA5LPGgfzmRmLOJCNogMACLhp6kLFVF4qzCy85Cfjs&v=1&r=http%3a%2f%2fwww.nbcnews.com%2fnews%2fus-news%2fu-s-denies-russian-claim-dialogue-between-two-nations-frozen-n698811&p=DevEx,5020.1",
	      "image": {
	        "thumbnail": {
	          "contentUrl": "https://www.bing.com/th?id=ON.03069B54CF0D6E6BA16C37F0365D8807&pid=News",
	          "width": 518,
	          "height": 700
	        }
	      },
	      "description": "A bizarre diplomatic disagreement erupted Wednesday when the Kremlin insisted that relations between Russia and the U.S. had all but ceased, prompting Washington to provide evidence rebutting that assertion. &quot;Almost every level of dialogue with the United ...",
	      "about": [
	        {
	          "readLink": "https://api.cognitive.microsoft.com/api/v5/entities/90fd2ef2-8123-de59-2b1f-ccd033454a4a",
	          "name": "NBC News"
	        },
	        {
	          "readLink": "https://api.cognitive.microsoft.com/api/v5/entities/ed4fce79-8ad4-352b-205b-e4db36c49bbe",
	          "name": "Russia"
	        },
	        {
	          "readLink": "https://api.cognitive.microsoft.com/api/v5/entities/01b4d947-dfd3-e107-170e-c210ac8a590e",
	          "name": "Frozen"
	        }
	      ],
	      "provider": [
	        {
	          "_type": "Organization",
	          "name": "NBC News"
	        }
	      ],
	      "datePublished": "2016-12-21T21:38:00",
	      "headline": true
	    },
	    {
	      "name": "Congregation member arrested in black church burning in Mississippi",
	      "url": "http://www.bing.com/cr?IG=9036717B9C984A7CABD52A1D79125D5A&CID=11DDC89CCCDA675D3B05C16ECDEB66B1&rd=1&h=aN0gBnKdqAW-c61SmgUm3pw7SL0Gt04x32CvIDeHku0&v=1&r=http%3a%2f%2fwww.cbsnews.com%2fnews%2fandrew-mcclinton-arrested-in-burning-of-black-church-in-mississippi%2f&p=DevEx,5022.1",
	      "image": {
	        "thumbnail": {
	          "contentUrl": "https://www.bing.com/th?id=ON.23297466A90BF0970B5ED2FBF038D1A6&pid=News",
	          "width": 620,
	          "height": 350
	        }
	      },
	      "description": "Andrew McClinton, left, was arrested for burning the Hopewell Baptist Church in Greenville, Mississippi on Nov. 1. He also allegedly graffitied the words “Vote Trump.” JACKSON, Miss. -- Mississippi authorities arrested a man Wednesday in the burning of ...",
	      "about": [
	        {
	          "readLink": "https://api.cognitive.microsoft.com/api/v5/entities/1a466af2-ed23-25bd-794d-1ca925e4681b",
	          "name": "Donald Trump"
	        },
	        {
	          "readLink": "https://api.cognitive.microsoft.com/api/v5/entities/00783077-ed13-a652-25c5-c1e11327e065",
	          "name": "Hopewell Baptist Church"
	        },
	        {
	          "readLink": "https://api.cognitive.microsoft.com/api/v5/entities/5076937a-574f-a4dc-deae-413ef79e1388",
	          "name": "Andrew County"
	        }
	      ],
	      "provider": [
	        {
	          "_type": "Organization",
	          "name": "CBS News"
	        }
	      ],
	      "datePublished": "2016-12-21T22:44:00",
	      "category": "US",
	      "headline": true
	    },
	    {
	      "name": "Obamacare Enrollments Exceed 6 Million, Outpacing Last Year&#39;s Sign-Ups",
	      "url": "http://www.bing.com/cr?IG=9036717B9C984A7CABD52A1D79125D5A&CID=11DDC89CCCDA675D3B05C16ECDEB66B1&rd=1&h=gOr2jhsO7bW1yHvw9R9ouNMnz0m-qKzuvEqKCaoUumU&v=1&r=http%3a%2f%2fwww.huffingtonpost.com%2fentry%2fobamacare-enrollments-outpacing-last-years-sign-ups_us_585ae559e4b0d9a59456f61c&p=DevEx,5024.1",
	      "image": {
	        "thumbnail": {
	          "contentUrl": "https://www.bing.com/th?id=ON.B59CB696E79C3DDE331E8657C884E459&pid=News",
	          "width": 700,
	          "height": 350
	        }
	      },
	      "description": "WASHINGTON ― Health insurance enrollments on the federal exchange marketplaces served by HealthCare.gov are outpacing last year’s sign-ups, Health and Human Services Secretary Sylvia Burwell announced Wednesday. As of Monday’s deadline for customers ...",
	      "about": [
	        {
	          "readLink": "https://api.cognitive.microsoft.com/api/v5/entities/54fbb499-2697-3d92-0142-3f0b6202096f",
	          "name": "Ups"
	        },
	        {
	          "readLink": "https://api.cognitive.microsoft.com/api/v5/entities/66d0f4c3-984d-0624-c823-b99f24746efb",
	          "name": "Patient Protection and Affordable Care Act"
	        }
	      ],
	      "provider": [
	        {
	          "_type": "Organization",
	          "name": "The Huffington Post"
	        }
	      ],
	      "datePublished": "2016-12-21T22:19:00",
	      "category": "Health",
	      "headline": true
	    },
	    {
	      "name": "Showdown looms between Trump administration, sanctuary cities",
	      "url": "http://www.bing.com/cr?IG=9036717B9C984A7CABD52A1D79125D5A&CID=11DDC89CCCDA675D3B05C16ECDEB66B1&rd=1&h=ECvw69OD_NHUZ72hZOv7fs8_5D574UuPDWW_BFNncmY&v=1&r=http%3a%2f%2fwww.foxnews.com%2fpolitics%2f2016%2f12%2f21%2fshowdown-looms-between-trump-administration-sanctuary-cities.html&p=DevEx,5035.1",
	      "image": {
	        "thumbnail": {
	          "contentUrl": "https://www.bing.com/th?id=ON.5FEF381953181C910A8E48220A2F527E&pid=News",
	          "width": 700,
	          "height": 393
	        }
	      },
	      "description": "As President-elect Donald Trump prepares to take office, a major showdown looms between his administration and cities across the country over one of his hallmark campaign issues: illegal immigration. At the Southern border, agents are on pace to apprehend ...",
	      "about": [
	        {
	          "readLink": "https://api.cognitive.microsoft.com/api/v5/entities/1a466af2-ed23-25bd-794d-1ca925e4681b",
	          "name": "Donald Trump"
	        },
	        {
	          "readLink": "https://api.cognitive.microsoft.com/api/v5/entities/c18d21d3-d615-c05e-a4a1-e63500236e41",
	          "name": "Showdown"
	        },
	        {
	          "readLink": "https://api.cognitive.microsoft.com/api/v5/entities/273e0477-30ed-3993-f232-f39244306805",
	          "name": "Fox News Channel"
	        }
	      ],
	      "provider": [
	        {
	          "_type": "Organization",
	          "name": "FOX News"
	        }
	      ],
	      "datePublished": "2016-12-21T21:24:00",
	      "category": "Politics",
	      "headline": true,
	      "clusteredArticles": [
	        {
	          "name": "Trump Reversal of Obama&#39;s Drilling Ban May Be Difficult, Experts Say",
	          "url": "http://www.bing.com/cr?IG=9036717B9C984A7CABD52A1D79125D5A&CID=11DDC89CCCDA675D3B05C16ECDEB66B1&rd=1&h=2NRBavFxUpGYP33kP_AdGFONQMNOGmI7_qji8J7SnEg&v=1&r=http%3a%2f%2fabcnews.go.com%2fPolitics%2ftrump-reversal-obamas-drilling-ban-difficult-experts%2fstory%3fid%3d44323535&p=DevEx,5036.1",
	          "description": "The announcement by the Obama administration that vast areas of Arctic and Atlantic oceans are off-limits to offshore drilling may be harder for President-elect Trump to overturn than other actions the president has taken. Trump transition communications ...",
	          "provider": [
	            {
	              "_type": "Organization",
	              "name": "ABC News"
	            }
	          ],
	          "datePublished": "2016-12-21T22:04:00",
	          "category": "US",
	          "headline": true
	        },
	        {
	          "name": "Billionaire Carl Icahn To Be Trump&#39;s Adviser On Regulatory Overhaul",
	          "url": "http://www.bing.com/cr?IG=9036717B9C984A7CABD52A1D79125D5A&CID=11DDC89CCCDA675D3B05C16ECDEB66B1&rd=1&h=Ea0YX8IJ9QNfGpsceK73swZGDmiSQFara8pBU8WSzOc&v=1&r=http%3a%2f%2fwww.forbes.com%2fsites%2fnathanvardi%2f2016%2f12%2f21%2fbillionaire-carl-icahn-to-be-trumps-adviser-on-regulatory-overhaul%2f&p=DevEx,5037.1",
	          "description": "Carl Icahn, the billionaire investor who has been laying siege to corporate boards for decades, will be named special adviser to Donald Trump on overhauling regulations, according to a person familiar with the matter. For years, Icahn considered himself ...",
	          "about": [
	            {
	              "readLink": "https://api.cognitive.microsoft.com/api/v5/entities/1a466af2-ed23-25bd-794d-1ca925e4681b",
	              "name": "Donald Trump"
	            },
	            {
	              "readLink": "https://api.cognitive.microsoft.com/api/v5/entities/a8a5ddaa-e131-f777-220f-bdc5219ee65d",
	              "name": "Carl Icahn"
	            },
	            {
	              "readLink": "https://api.cognitive.microsoft.com/api/v5/entities/ac13d79c-7569-2321-455e-1c6f72a2f5a4",
	              "name": "Adviser"
	            }
	          ],
	          "provider": [
	            {
	              "_type": "Organization",
	              "name": "Forbes"
	            }
	          ],
	          "datePublished": "2016-12-21T22:47:00",
	          "headline": true
	        }
	      ]
	    }

 */
}
