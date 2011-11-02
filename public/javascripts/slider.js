$(document).ready(function(){
    // Variable to hold scroll type
    var slideDrag,
    // Width of .scroll-content ul
    slideWidth = 330,
    // Speed of animation in ms
    slideSpeed = 400,
    animated = false;
    
    // Initialize sliders
    $(".scroll-slider").slider({
        animate: slideSpeed,
        start: checkType,
        slide: doSlide,
        change: doSlide,
        max: slideWidth
    });

    // Set each slider to a value
    $(".scroll-slider").each(function(index){
        $(this).slider("value", 330 / 5 * index);
    });

    // You can also change a slider at any time like so:
    // $(".scroll-slider:eq(0)").slider("value", value);
    //
    // That would move the first slider to a value, along with its content
    function checkType(e){
        slideDrag = $(e.originalEvent.target).hasClass("ui-slider-handle");
    }
    
    function doSlide(e, ui){
        var target = $(e.target).next(".scroll-content"),
        // If sliders were above the content instead of below, we'd use:
        // target = $(e.target).next(".scroll-content")
        maxScroll = target.attr("scrollWidth") - target.width();

        // Need to check type now to prevent the new change handler from firing twice when user clicks on slider,
        // because both 'slide' and 'change' events are fired on a click, but only a 'change' when setting slider
        // value manually via code.
        if (e.type == 'slide'){
            // Was it a click or drag?
            if (slideDrag === true){
                // User dragged slider head, match position
                target.attr({scrollLeft: ui.value * (maxScroll / slideWidth) });
            }else{
                // User clicked on slider itself, animate to position
                target.stop().animate({scrollLeft: ui.value * (maxScroll / slideWidth) }, slideSpeed);
            }
            animated = true
        }else{
            if (animated === false){
                target.stop().animate({scrollLeft: ui.value * (maxScroll / slideWidth) }, slideSpeed);
            }
            animated = false;
        }
    }
});