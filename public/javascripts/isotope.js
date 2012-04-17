$('#race').isotope({ 
	itemSelector : '.race_content',
	layoutMode : 'fitRows',
	getSortData : {
		name : function ( $elem ) {
			return $elem.find('.sortname').text();
		}
	}
});
