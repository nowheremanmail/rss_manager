var PARAMS = {
	language : "es-es",
	day : null,
	word : null,
	wordsPage : 1,
	filter : "",
	feedPage : 1,
	feedFilter : "",
	feedId : "",
	datesPage : 1,
	verb : "/top",
	showAll : false
};

$(document)
		.ready(
				function() {
					$('#languages')
							.on(
									'pageshow',
									function(event, ui) {
										$.mobile.loading('show', {
											text : 'Please Wait',
											textVisible : true,
											theme : 'z',
											html : ""
										});

										$
												.ajax({
													type : "POST",
													url : "/languages",
													dataType : 'json'
												})
												.done(function(msg) {
													var json = msg;
													_loadLanguages(json);
													$.mobile.loading('hide');
												})
												.fail(
														function(jqXHR,
																textStatus,
																errorThrown) {
															$.mobile
																	.loading('hide');

															// show error
															// message
															$.mobile
																	.showPageLoadingMsg(
																			$.mobile.pageLoadErrorMessageTheme,
																			"Data cannot be loaded",
																			true);
															// hide after delay
															setTimeout(
																	$.mobile.hidePageLoadingMsg,
																	1500);
														});
									});

					$("#checkbox-mini").on('click', function(event, ui) {
						if (PARAMS.verb === "/top")
							PARAMS.verb = "/2top";
						else
							PARAMS.verb = "/top";

						byLanguageDayPageShow();
					});

					$("#checkbox-mini-all").on('click', function(event, ui) {
						if (PARAMS.showAll === true)
							PARAMS.showAll = false;
						else
							PARAMS.showAll = true;

						var _txt = $("#filterText").val();
						processFilter(_txt);
					});

					$('#filterButton').on('click', function(event, ui) {
						var _txt = $("#filterText").val();
						processFilter(_txt);
					});

					$('#feedDetailPage').on('pageshow', function(event, ui) {
						feedDetailPage();
					});

					$('#feedClusterPage').on('pageshow', function(event, ui) {
						clusterDetailPage();
					});
					
					$('#buttonClusterStart').on('click', function(event, ui) {
						sendCluster($("#languageClusterText").val(), $("#dayClusterText").val(), $("#loopClusterText").val(), $("#factorClusterText").val());
					});
					
					$('#feeds').on('pageshow', function(event, ui) {
						var _txt = $("#filterText").val();
						processFilter(_txt);
					});

					$('#processButton').on('click', function(event, ui) {
						var _txt = $("#processText").val();
						if (_txt && _txt != "") {
							processSubmit(_txt);
						}
					});
					$('#processOneButton').on('click', function(event, ui) {
						var _txt = $("#processOneText").val();
						if (_txt && _txt != "") {
							processOneSubmit(_txt);
						}
					});

					$('#buttonStart').on('click', function(event, ui) {
						updateFeed(PARAMS.feedId, "start", "");
					});

					$('#buttonChangeLanguage').on(
							'click',
							function(event, ui) {
								updateFeed(PARAMS.feedId, "language", $(
										"#languageText").val());
							});

					$('#buttonReprocess').on(
							'click',
							function(event, ui) {
								updateFeed(PARAMS.feedId, "reprocess", $(
										"#languageText").val());
							});

					// $('#buttonStatus').on('click', function(event, ui) {
					// updateFeed (PARAMS.feedId, "status",
					// $("#languageText").val());
					// });

					$('#moveLanguagePage').on('pageshow', function(event, ui) {
						$("#sourceLanguageText").val(PARAMS.language);// attr("value",
						// PARAMS.language);
					});

					$('#changeLanguageButton')
							.on(
									'click',
									function(event, ui) {
										var _txtDestination = $(
												"#destinationLanguageText")
												.val();
										var _txtSource = $(
												"#sourceLanguageText").val();

										if (_txtSource && _txtSource != ""
												&& _txtDestination
												&& _txtDestination != "") {
											$.mobile.loading('show', {
												text : 'Please Wait',
												textVisible : true,
												theme : 'z',
												html : ""
											});

											$
													.ajax(
															{
																type : "POST",
																url : "/changeLanguage",
																dataType : 'json',
																data : {
																	sourceLang : _txtSource,
																	destinationLang : _txtDestination
																}
															})
													.done(
															function(msg) {
																var json = msg;
																$.mobile
																		.loading('hide');
															})
													.fail(
															function(jqXHR,
																	textStatus,
																	errorThrown) {
																$.mobile
																		.loading('hide');

																// show error
																// message
																$.mobile
																		.showPageLoadingMsg(
																				$.mobile.pageLoadErrorMessageTheme,
																				"Data cannot be loaded",
																				true);
																// hide after
																// delay
																setTimeout(
																		$.mobile.hidePageLoadingMsg,
																		1500);
															});
										}
									});

					$('#searchButton').on('click', function(event, ui) {
						var _txt = $("#searchText").val();
						if (_txt && _txt != "") {
							txt = _txt;
							processSearch(txt);
						}
					});

					$('#byLanguageDayPage').on('pageshow',
							byLanguageDayPageShow);

					$('#wordDetailPage')
							.on(
									'pageshow',
									function(event, ui) {
										var lang = PARAMS.language;
										var day = PARAMS.day;
										var word = PARAMS.word;

										$.mobile.loading('show', {
											text : 'Please Wait',
											textVisible : true,
											theme : 'z',
											html : ""
										});

										$
												.ajax(
														{
															type : "POST",
															url : "/"
																	+ lang
																	+ "/"
																	+ day
																	+ "/2detail",
															dataType : 'json',
															data : {
																word : word
															}
														})
												.done(
														function(msg) {
															var json = msg;
															_wordDetail(lang,
																	day, word,
																	json);
															$.mobile
																	.loading('hide');
														})
												.fail(
														function(jqXHR,
																textStatus,
																errorThrown) {
															$.mobile
																	.loading('hide');

															// show error
															// message
															$.mobile
																	.showPageLoadingMsg(
																			$.mobile.pageLoadErrorMessageTheme,
																			"Data cannot be loaded",
																			true);
															// hide after delay
															setTimeout(
																	$.mobile.hidePageLoadingMsg,
																	1500);
														});
									});

					$('#wordsPage').on('pageshow', loadWordPage);

					$('#byLanguagePage').on('pageshow', showDays);
				});

function byLanguageDayPageShow(event, ui) {
	var lang = PARAMS.language;
	var day = PARAMS.day;

	$.mobile.loading('show', {
		text : 'Please Wait',
		textVisible : true,
		theme : 'z',
		html : ""
	});

	$.ajax({
		type : "POST",
		url : "/" + lang + "/" + day + PARAMS.verb,
		dataType : 'json'
	}).done(function(msg) {
		var json = msg;
		_showTotalWords(lang, day, json);
		$.mobile.loading('hide');
	}).fail(
			function(jqXHR, textStatus, errorThrown) {
				$.mobile.loading('hide');

				// show error
				// message
				$.mobile.showPageLoadingMsg($.mobile.pageLoadErrorMessageTheme,
						"Data cannot be loaded", true);
				// hide after delay
				setTimeout($.mobile.hidePageLoadingMsg, 1500);
			});
}

function processSearch(txt) {
	if (txt.length > 0) {
		PARAMS.wordsPage = 1;
		PARAMS.filter = txt;
		loadWordPage();
	}

}

function showDays(event, ui) {
	var lang = PARAMS.language;

	$.mobile.loading('show', {
		text : 'Please Wait',
		textVisible : true,
		theme : 'z',
		html : ""
	});

	$.ajax({
		type : "POST",
		url : "/" + lang + "/days",
		dataType : 'json',
		data : {
			page : PARAMS.datesPage
		}
	}).done(function(msg) {
		var json = msg;
		_showDetail(lang, json);
		$.mobile.loading('hide');
	}).fail(
			function(jqXHR, textStatus, errorThrown) {
				$.mobile.loading('hide');

				// show error
				// message
				$.mobile.showPageLoadingMsg($.mobile.pageLoadErrorMessageTheme,
						"Data cannot be loaded", true);
				// hide after delay
				setTimeout($.mobile.hidePageLoadingMsg, 1500);
			});
}

function processFilter(txt) {
	PARAMS.feedFilter = txt;
	PARAMS.feedPage = 1;

	loadWithFilter();
}

function clusterDetailPage() {
	$("#languageClusterText").val(PARAMS.language);
	$("#dayClusterText").val(PARAMS.day);
//	$("#factorClusterText").val("10000");
//	$("#loopsClusterText").val("0.5");
}

function feedDetailPage() {
	$.mobile.loading('show', {
		text : 'Please Wait',
		textVisible : true,
		theme : 'z',
		html : ""
	});

	$.ajax({
		type : "POST",
		url : "/feedDetails",
		dataType : 'json',
		data : {
			url : PARAMS.feedId
		}
	}).done(function(msg) {
		var json = msg;

		$("#urlText").val(json.u);
		$("#languageText").val(json.i);
		$("#lastDateText").val(json.d);
		$("#lastUpdateText").val(json.l);
		$("#lastMessageText").val(json.x);
		/*
		 * d: "10 May 2016 21:28:16 GMT" e: true i: "es-es" l: "Tue, 10 May 2016
		 * 23:21:54 +0200" r: 5 t: "Portada de EL PAÍS" u:
		 * "http://ep00.epimg.net/rss/elpais/portada.xml" x: ""
		 */

		$.mobile.loading('hide');
	}).fail(
			function(jqXHR, textStatus, errorThrown) {
				$.mobile.loading('hide');

				// show error message
				$.mobile.showPageLoadingMsg($.mobile.pageLoadErrorMessageTheme,
						"Data cannot be loaded", true);
				// hide after delay
				setTimeout($.mobile.hidePageLoadingMsg, 1500);
			});
}

function loadWithFilter() {
	$.mobile.loading('show', {
		text : 'Please Wait',
		textVisible : true,
		theme : 'z',
		html : ""
	});

	$.ajax(
			{
				type : "POST",
				url : PARAMS.showAll ? "/all/feeds"
						: (PARAMS.language !== null ? "/" + PARAMS.language
								+ "/feeds" : "/all/feeds"),
				dataType : 'json',
				data : {
					filter : PARAMS.feedFilter,
					page : PARAMS.feedPage
				}
			}).done(function(msg) {
		var json = msg;
		_loadFeeds(json);
		$.mobile.loading('hide');
	}).fail(
			function(jqXHR, textStatus, errorThrown) {
				$.mobile.loading('hide');

				// show error message
				$.mobile.showPageLoadingMsg($.mobile.pageLoadErrorMessageTheme,
						"Data cannot be loaded", true);
				// hide after delay
				setTimeout($.mobile.hidePageLoadingMsg, 1500);
			});
}

function processSubmit(txt) {
	if (txt.length > 0) {
		$.mobile.changePage('#processPageResult');

		$.mobile.loading('show', {
			text : 'Please Wait',
			textVisible : true,
			theme : 'z',
			html : ""
		});

		$.ajax({
			type : "POST",
			url : "/addHtml",
			dataType : 'json',
			data : {
				url : txt
			}
		}).done(function(msg) {
			var json = msg;
			_processSubmit(json);
			$.mobile.loading('hide');

		}).fail(
				function(jqXHR, textStatus, errorThrown) {
					$.mobile.loading('hide');

					// show error message
					$.mobile.showPageLoadingMsg(
							$.mobile.pageLoadErrorMessageTheme,
							"Data cannot be loaded", true);
					// hide after delay
					setTimeout($.mobile.hidePageLoadingMsg, 1500);
				});
	}

}

function processOneSubmit(txt) {
	if (txt.length > 0) {
		// $.mobile.changePage('#processPageResult');

		$.mobile.loading('show', {
			text : 'Please Wait',
			textVisible : true,
			theme : 'z',
			html : ""
		});

		$.ajax({
			type : "POST",
			url : "/addRss",
			dataType : 'json',
			data : {
				url : txt
			}
		}).done(function(msg) {
			var json = msg;
			// _processSubmit(json);
			$.mobile.loading('hide');

		}).fail(
				function(jqXHR, textStatus, errorThrown) {
					$.mobile.loading('hide');

					// show error message
					$.mobile.showPageLoadingMsg(
							$.mobile.pageLoadErrorMessageTheme,
							"Data cannot be loaded", true);
					// hide after delay
					setTimeout($.mobile.hidePageLoadingMsg, 1500);
				});
	}

}

function _processSubmit(json) {
	var items = json.l;
	var list = $("#processResultList");
	list.html("");

	for ( var i in items) {
		var tmp = items[i];
		var row = $('<li></li>'); // .addClass('row');

		var link0 = $('<A></A>').attr("href", "#").attr("data-ajax", "false");
		row.append(link0);

		var cel0 = $('<h3></h3>').text(tmp.u);
		var cel3 = $('<p></p>').text(tmp.m);

		link0.append(cel0);
		link0.append(cel3);

		list.append(row);
	}

	list.listview('refresh')
}

function _showDetail(lang, json) {
	var items = json;
	var list = $("#byLanguagePageList");
	list.html("");

	if (PARAMS.datesPage > 1) {
		var row = $('<li></li>'); // .addClass('row');
		var link0 = $('<A></A>').attr("href", "#").attr("data-ajax", "false");
		row.append(link0);

		var cel0 = $('<h3></h3>').text("PREVIOUS PAGE");
		link0.append(cel0);
		link0.on("click", function(a) {
			if (PARAMS.datesPage > 1) {
				PARAMS.datesPage--;
				showDays();
			}
		});
		list.append(row);
	}

	for ( var i in items) {
		var tmp = items[i];
		var row = $('<li></li>'); // .addClass('row');

		var link0 = $('<A></A>').attr("href", "#").attr("id", tmp).attr(
				"data-ajax", "false");
		row.append(link0);

		link0.on("click", function(a) {
			PARAMS.day = this.id;
			$.mobile.changePage("#byLanguageDayPage");
		});

		var cel3 = $('<p></p>');
		cel3.text(tmp.substr(0, 4) + "-" + tmp.substr(4, 2) + "-"
				+ tmp.substr(6, 2));

		link0.append(cel3);

		var tt = $('<a></a>').attr("href", "#").attr("id", tmp).text("Cluster");
		tt.on("click", function(a) {
			PARAMS.day = this.id;
			$.mobile.changePage("#feedClusterPage");
		});
		
		row.append(tt);
		
		list.append(row);
	}

	{
		var row = $('<li></li>'); // .addClass('row');
		var link0 = $('<A></A>').attr("href", "#").attr("data-ajax", "false");
		row.append(link0);

		var cel0 = $('<h3></h3>').text("NEXT PAGE");
		link0.append(cel0);
		link0.on("click", function(a) {
			PARAMS.datesPage++;
			showDays();
		});
		list.append(row);
	}

	list.listview('refresh')
}

function _showTotalWords(lang, day, json) {
	var items = json;
	var list = $("#byLanguageDayPageList");
	list.html("");

	for ( var i in items) {
		var tmp = items[i];

		var row = $('<li data-role="list-divider"></li>').text(tmp.n);
		list.append(row);

		var res = "";
		for ( var x in tmp.l) {
			row = $('<li></li>');
			var ll = tmp.l[x];
			var link0 = $('<A></A>').attr("href", "#").attr("id", ll).text(
					ll.replace("|", " "));
			link0.on("click", function(a) {
				PARAMS.word = this.id;
				$.mobile.changePage("#wordDetailPage");
			});

			var tt = $('<a></a>').attr("href", "#").attr("id", ll).text("Hide");
			tt.on("click", function(a) {
				updateWord(PARAMS.language, this.id, "none");
			});

			row.append(link0);
			row.append(tt);
			list.append(row);
		}
	}

	list.listview('refresh')
}

function _showTotalWords2(lang, day, json) {
	var items = json;
	var list = $("#byLanguageDayPageList");
	list.html("");

	for ( var i in items) {
		var tmp = items[i];
		var row = $('<li></li>'); // .addClass('row');
		var res = "";
		for ( var x in tmp.l) {
			var ll = tmp.l[x];
			if (res !== "") {
				res += ", " + ll;
			} else {
				res = ll;
			}
		}
		var link0 = $('<A></A>').attr("href", "#").text(res);
		var cel0 = $('<span class="ui-li-count">' + tmp.n + '</span>');

		link0.append(cel0);

		row.append(link0);
		list.append(row);
	}

	list.listview('refresh')
}

function _loadLanguages(json) {
	var items = json;
	var list = $("#listLanguages");
	list.html("");

	for ( var i in items) {
		var tmp = items[i];
		var row = $('<li></li>');
		var link0 = $('<A data-rel="back"></A>')
				.attr("href", "#byLanguagePage").attr("id", tmp).text(tmp);
		row.append(link0);

		link0.on("click", function(a) {
			PARAMS.language = this.id;
			$("#optionLang").text("Select Language (" + PARAMS.language + ")");
			// $.mobile.changePage("#byLanguagePage");
		});

		list.append(row);

	}

	list.listview('refresh')
}

function _loadFeeds(json) {
	var items = json;
	var list = $("#listFeeds");
	list.html("");

	if (PARAMS.feedPage > 1) {
		var row = $('<li></li>'); // .addClass('row');
		var link0 = $('<A></A>').attr("href", "#").attr("data-ajax", "false");
		row.append(link0);

		var cel0 = $('<h3></h3>').text("PREVIOUS PAGE");
		link0.append(cel0);
		link0.on("click", function(a) {
			if (PARAMS.feedPage > 1) {
				PARAMS.feedPage--;
				loadWithFilter();
			}
		});
		list.append(row);
	}

	for ( var i in items) {
		var tmp = items[i];
		var row = $('<li></li>'); // .addClass('row');
		var link0 = $('<A></A>')
				.attr(
						"href",
						(tmp.u.startsWith("http://") || tmp.u
								.startsWith("https://")) ? tmp.u : "#").attr(
						"rel", "external").attr("data-ajax", "false").attr(
						"target", "new");
		row.append(link0);

		var cel0 = $('<h3></h3>').text(tmp.u);
		var cel3 = $('<p></p>').text(tmp.x); // redx
		var cel2 = $('<p class="ui-li-aside"></p>').text(tmp.d); // redx

		var tt = $('<a></a>').attr("href", "#feedDetailPage").attr("id", tmp.u)
				.text("Detail");
		tt.on("click", function(a) {
			PARAMS.feedId = this.id;
			$.mobile.changePage("#feedDetailPage");
			// showDetail(this.id);
		});
		// var tt = $('<a href="#detailFeed" data-transition="pop"
		// data-rel="popup" data-position-to="window">Details</a>');

		link0.append(cel0);
		link0.append(cel3);
		link0.append(cel2);
		row.append(tt);

		list.append(row);

	}

	{
		var row = $('<li></li>'); // .addClass('row');
		var link0 = $('<A></A>').attr("href", "#").attr("data-ajax", "false");
		row.append(link0);

		var cel0 = $('<h3></h3>').text("NEXT PAGE");
		link0.append(cel0);
		link0.on("click", function(a) {
			PARAMS.feedPage++;
			loadWithFilter();
		});
		list.append(row);
	}

	// .enhanceWithin()
	list.listview('refresh')
}

function _wordDetail(lang, day, word, json) {
	var items = json;
	var list = $("#wordDetailList");
	list.html("");

	for ( var i in items) {
		var tmp = items[i];
		var row = $('<li></li>'); // .addClass('row');
		var link0 = $('<A></A>')
				.attr(
						"href",
						(tmp.u.startsWith("http://") || tmp.u
								.startsWith("https://")) ? tmp.u : "#").attr(
						"rel", "external").attr("data-ajax", "false").attr(
						"target", "new");
		row.append(link0);

		var cel0 = $('<h3>'
				+ tmp.t.replace(word, "<strong>" + word + "</strong>")
				+ '</h3>');
		link0.append(cel0);
		var cel1 = $('<p></p>').text(tmp.s);
		link0.append(cel1);

		var tt = $('<a></a>').attr("href", "#feedDetailPage").attr("id", tmp.s)
				.text("Detail");
		tt.on("click", function(a) {
			PARAMS.feedId = this.id;
			$.mobile.changePage("#feedDetailPage");
			// showDetail(this.id);
		});
		row.append(tt);

		list.append(row);

	}
	// .enhanceWithin()
	list.listview('refresh')
}

function _wordsDetail(lang, json) {
	var items = json;
	var list = $("#wordsList");
	list.html("");

	if (PARAMS.wordsPage > 1) {
		var row = $('<li></li>'); // .addClass('row');
		var link0 = $('<A></A>').attr("href", "#").attr("data-ajax", "false");
		row.append(link0);

		var cel0 = $('<h3></h3>').text("PREVIOUS PAGE");
		link0.append(cel0);
		link0.on("click", function(a) {
			if (PARAMS.wordsPage > 1) {
				PARAMS.wordsPage--;
				loadWordPage();
			}
		});
		list.append(row);
	}

	for ( var i in items) {
		var tmp = items[i];
		var row = $('<li></li>'); // .addClass('row');
		var link0 = $('<A></A>').attr("href", "#").attr("data-ajax", "false")
				.attr("id", tmp.w).attr("c", tmp.c !== null ? tmp.c : "");
		row.append(link0);

		var cel0 = $('<h3></h3>').text(tmp.w);
		link0.append(cel0);
		var cel1 = $('<p></p>').text(tmp.c);
		link0.append(cel1);

		link0.on("click", function(a) {

			updateWord(PARAMS.language, this.id,
					$(this).attr("c") === "none" ? "" : "none", true);
		});

		list.append(row);

	}
	{
		var row = $('<li></li>'); // .addClass('row');
		var link0 = $('<A></A>').attr("href", "#").attr("data-ajax", "false");
		row.append(link0);

		var cel0 = $('<h3></h3>').text("NEXT PAGE");
		link0.append(cel0);
		link0.on("click", function(a) {
			PARAMS.wordsPage++;
			loadWordPage();
		});
		list.append(row);
	}

	// .enhanceWithin()
	list.listview('refresh')
}

function updateWord(lang, _word, _cat, oooo) {
	$.mobile.loading('show', {
		text : 'Please Wait',
		textVisible : true,
		theme : 'z',
		html : ""
	});

	$.ajax({
		type : "POST",
		url : "/" + lang + "/updateWord",
		dataType : 'json',
		data : {
			word : _word,
			category : _cat
		}
	}).done(function(msg) {
		var json = msg;
		// _loadFeeds(json);
		$.mobile.loading('hide');
		if (oooo)
			$.mobile.changePage("#wordsPage");
	}).fail(
			function(jqXHR, textStatus, errorThrown) {
				$.mobile.loading('hide');

				// show error message
				$.mobile.showPageLoadingMsg($.mobile.pageLoadErrorMessageTheme,
						"Data cannot be loaded", true);
				// hide after delay
				setTimeout($.mobile.hidePageLoadingMsg, 1500);
			});

}

function sendCluster(lang, day, _loops, _factor) {
	$.mobile.loading('show', {
		text : 'Please Wait',
		textVisible : true,
		theme : 'z',
		html : ""
	});

	$.ajax({
		type : "POST",
		url : "/" + lang + "/" + day + "/cluster",
		dataType : 'json',
		data : {
			loops : _loops,
			factor : _factor
		}
	}).done(function(msg) {
		var json = msg;
		// _loadFeeds(json);
		$.mobile.loading('hide');
	}).fail(
			function(jqXHR, textStatus, errorThrown) {
				$.mobile.loading('hide');

				// show error message
				$.mobile.showPageLoadingMsg($.mobile.pageLoadErrorMessageTheme,
						"Data cannot be loaded", true);
				// hide after delay
				setTimeout($.mobile.hidePageLoadingMsg, 1500);
			});

}


function loadWordPage(event, ui) {
	var lang = PARAMS.language;
	var nPage = PARAMS.wordsPage;
	var pFilter = PARAMS.filter;

	$.mobile.loading('show', {
		text : 'Please Wait',
		textVisible : true,
		theme : 'z',
		html : ""
	});

	$.ajax({
		type : "POST",
		url : "/" + lang + "/words",
		dataType : 'json',
		data : {
			page : nPage,
			filter : pFilter
		}
	}).done(function(msg) {
		var json = msg;
		_wordsDetail(lang, json);
		$.mobile.loading('hide');
	}).fail(
			function(jqXHR, textStatus, errorThrown) {
				$.mobile.loading('hide');

				// show error message
				$.mobile.showPageLoadingMsg($.mobile.pageLoadErrorMessageTheme,
						"Data cannot be loaded", true);
				// hide after delay
				setTimeout($.mobile.hidePageLoadingMsg, 1500);
			});
}

function updateFeed(_url, _oper, _param) {
	$.mobile.loading('show', {
		text : 'Please Wait',
		textVisible : true,
		theme : 'z',
		html : ""
	});

	$.ajax({
		type : "POST",
		url : "/updateFeed",
		dataType : 'json',
		data : {
			url : _url,
			oper : _oper,
			param : _param
		}
	}).done(function(msg) {
		var json = msg;
		// _loadFeeds(json);
		$.mobile.loading('hide');
		// $.mobile.changePage("#wordsPage");
	}).fail(
			function(jqXHR, textStatus, errorThrown) {
				$.mobile.loading('hide');

				// show error message
				$.mobile.showPageLoadingMsg($.mobile.pageLoadErrorMessageTheme,
						"Data cannot be loaded", true);
				// hide after delay
				setTimeout($.mobile.hidePageLoadingMsg, 1500);
			});

}