(function ($j) {
    $j.fn.showHide = function (options) {

        //default vars for the plugin
        var defaults = {
            speed: 1000,
            easing: '',
            changeText: 0,
            showText: 'Show',
            hideText: 'Hide'

        };
        var options = $j.extend(defaults, options);

        $j(this).click(function (event) {
            event.preventDefault();

            // optionally add the class .toggleDiv to each div you want to automatically close
            $j('.toggleDiv:hidden').slideUp(options.speed, options.easing);

            // this var stores which button you've clicked
            var toggleClick = $j(this);

            // this reads the rel attribute of the button to determine which div id to toggle
            var toggleDiv = $j(this).attr('rel');

            // here we toggle show/hide the correct div at the right speed and using which easing effect
            $j(toggleDiv).slideToggle(options.speed, options.easing, function () {

                console.log("ToggleClick = " + toggleClick);

                // this only fires once the animation is completed
                if (options.changeText == 1) {
                    $j(toggleDiv).is(":visible") ? toggleClick.text(options.hideText) : toggleClick.text(options.showText);
                }
            });
        });

    };
})(jQuery);