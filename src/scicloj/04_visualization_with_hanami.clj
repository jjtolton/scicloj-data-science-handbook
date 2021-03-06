(ns scicloj.04-visualization-with-hanami
  (:require [notespace.api :as notespace]
            [notespace.kinds :as kind]
            [tech.v3.datatype.functional :as dtype]))

;; Notespace
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Listen for changes in the namespace and update notespace automatically
;; Hidden kinds should not show in the notespace page
^kind/hidden
(comment
  ;; Manually start an empty notespace
  (notespace/init-with-browser)

  ;; Renders the notes and listens to file changes
  (notespace/listen)

  ;; Clear an existing notespace browser
  (notespace/init)

  ;; Evaluating a whole notespace
  (notespace/eval-this-notespace)
  )

;; Chapter 04 - Visualization with Hanami
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;

["# Visualization with Hanami"]

["This chapter will dive into practical aspects of visualizing data using the Clojure library Hanami and other tools (TODO)"]

["## General Matplotlib Tips"

 "Before we dive into the details of creating visualizations with Hanami, there are a few useful things you should know about using the package."]

["### Importing Hanami"]

(require '[aerial.hanami.common :as hanami-common]
         '[aerial.hanami.templates :as hanami-templates]
         '[tech.v3.datatype.functional :as dtype-func])



["### Setting Styles"
 "We will use the plt.style directive to choose appropriate aesthetic styles for our figures. Here we will set the classic style, which ensures that the plots we create use the classic Matplotlib style:"]

;;todo

["Throughout this section, we will adjust this style as needed. Note that the stylesheets used here are supported as of Matplotlib version 1.5; if you are using an earlier version of Matplotlib, only the default style is available. For more information on stylesheets, see Customizing Matplotlib: Configurations and Style Sheets."]

["### `show()` or `No show()`? How to Display Your Plots"

 "A visualization you can't see won't be of much use, but just how you view your Matplotlib plots depends on the context. The best use of Matplotlib differs depending on how you are using it; roughly, the three applicable contexts are using Matplotlib in a script, in an IPython terminal, or in an IPython notebook."]

["#### Plotting from a script"

 "If you are using Matplotlib from within a script, the function plt.show() is your friend. plt.show() starts an event loop, looks for all currently active figure objects, and opens one or more interactive windows that display your figure or figures.

So, for example, you may have a file called myplot.py containing the following:"
 ]
;; todo
["You can then run this script from the command-line prompt, which will result in a window opening with your figure displayed:"]
;; todo
["The plt.show() command does a lot under the hood, as it must interact with your system's interactive graphical backend. The details of this operation can vary greatly from system to system and even installation to installation, but matplotlib does its best to hide all these details from you."
 "One thing to be aware of: the plt.show() command should be used only once per Python session, and is most often seen at the very end of the script. Multiple show() commands can lead to unpredictable backend-dependent behavior, and should mostly be avoided."]

["#### Plotting from an IPython shell"
 "It can be very convenient to use Matplotlib interactively within an IPython shell (see IPython: Beyond Normal Python). IPython is built to work well with Matplotlib if you specify Matplotlib mode. To enable this mode, you can use the %matplotlib magic command after starting ipython:"]
;; todo

["At this point, any plt plot command will cause a figure window to open, and further commands can be run to update the plot. Some changes (such as modifying properties of lines that are already drawn) will not draw automatically: to force an update, use plt.draw(). Using plt.show() in Matplotlib mode is not required."]


["#### Plotting from an Notespace notebook"
 "The IPython notebook is a browser-based interactive data analysis tool that can combine narrative, code, graphics, HTML elements, and much more into a single executable document (see IPython: Beyond Normal Python).

Plotting interactively within an IPython notebook can be done with the %matplotlib command, and works in a similar way to the IPython shell. In the IPython notebook, you also have the option of embedding graphics directly in the notebook, with two possible options:

    %matplotlib notebook will lead to interactive plots embedded within the notebook
    %matplotlib inline will lead to static images of your plot embedded in the notebook

After running this command (it needs to be done only once per kernel/session), any cell within the notebook that creates a plot will embed a PNG image of the resulting graphic:

TO-DO need to clean all this:"]

(def x-range (range 0 10 1/10))

(def my-figure
  (->
   []
   (into (map #(identity {:x % :y (dtype-func/sin %) :label "sin"}) x-range))
   (into (map #(identity {:x % :y (dtype-func/cos %) :label "cos"}) x-range))
   (#(hanami-common/xform
      hanami-templates/line-chart
      :WIDTH 600 :HEIGHT 500
      :ENCODINGS {:X :x
                  :Y :y}
      :DATA %))
   (#(assoc-in %
               [:encoding :strokeDash]
               {:field :label
                :type "nominal"}))))

^kind/vega
my-figure

["### Saving Figures to File

One nice feature of Matplotlib is the ability to save figures in a wide variety of formats. Saving a figure can be done using the savefig() command. For example, to save the previous figure as a PNG file, you can run this:"]

(require '[applied-science.darkstar :as darkstar]
         '[clojure.data.json :as json])


(defn save-plot! [m & [filename]]
  (let [svg-render (if (= (:MODE m) "vega")
                 darkstar/vega-spec->svg
                 darkstar/vega-lite-spec->svg)]
   (->>
    m
    json/write-str
    svg-render
    (spit (or filename "plot.svg")))))


(save-plot! my-figure "my_figure.svg")

["TO-DO render png"]

["We now have a file called my_figure.png in the current working directory:"]

^kind/void
["!ls -lh my_figure.png"] 


["To confirm that it contains what we think it contains, let's use the IPython Image object to display the contents of this file:"]

^kind/void
["from IPython.display import Image
  Image('my_figure.png')"] 

["In savefig(), the file format is inferred from the extension of the given filename. Depending on what backends you have installed, many different file formats are available. The list of supported file types can be found for your system by using the following method of the figure canvas object:"]

^kind/hiccup
[:pre "fig.canvas.get_supported_filetypes()"] 

^kind/void
["{'eps': 'Encapsulated Postscript',
 'jpeg': 'Joint Photographic Experts Group',
 'jpg': 'Joint Photographic Experts Group',
 'pdf': 'Portable Document Format',
 'pgf': 'PGF code for LaTeX',
 'png': 'Portable Network Graphics',
 'ps': 'Postscript',
 'raw': 'Raw RGBA bitmap',
 'rgba': 'Raw RGBA bitmap',
 'svg': 'Scalable Vector Graphics',
 'svgz': 'Scalable Vector Graphics',
 'tif': 'Tagged Image File Format',
 'tiff': 'Tagged Image File Format'}"]

["Note that when saving your figure, it's not necessary to use plt.show() or related commands discussed earlier."]

["## Two Interfaces for the Price of One¶

A potentially confusing feature of Matplotlib is its dual interfaces: a convenient MATLAB-style state-based interface, and a more powerful object-oriented interface. We'll quickly highlight the differences between the two here.

### MATLAB-style interface

Matplotlib was originally written as a Python alternative for MATLAB users, and much of its syntax reflects that fact. The MATLAB-style tools are contained in the pyplot (plt) interface. For example, the following code will probably look quite familiar to MATLAB users:"]

^kind/void
["plt.figure()  # create a plot figure

  # create the first of two panels and set current axis
  plt.subplot(2, 1, 1) # (rows, columns, panel number)
  plt.plot(x, np.sin(x))

  # create the second panel and set current axis
  plt.subplot(2, 1, 2)
  plt.plot(x, np.cos(x));"]


^kind/vega
(->
 my-figure
 (assoc :height 150)
 (assoc-in [:encoding :row] {:field :label
                             :type "nominal"}))

["### Object-oriented interface

The object-oriented interface is available for these more complicated situations, and for when you want more control over your figure. Rather than depending on some notion of an \"active\" figure or axes, in the object-oriented interface the plotting functions are methods of explicit Figure and Axes objects. To re-create the previous plot using this style of plotting, you might do the following:"]

^kind/void
["# First create a grid of plots
  # ax will be an array of two Axes objects
  fig, ax = plt.subplots(2)

  # Call plot() method on the appropriate object
  ax[0].plot(x, np.sin(x))
  ax[1].plot(x, np.cos(x));"]


["For more simple plots, the choice of which style to use is largely a matter of preference, but the object-oriented approach can become a necessity as plots become more complicated. Throughout this chapter, we will switch between the MATLAB-style and object-oriented interfaces, depending on what is most convenient. In most cases, the difference is as small as switching plt.plot() to ax.plot(), but there are a few gotchas that we will highlight as they come up in the following sections."]


["# Simple Line Plots"]

["Perhaps the simplest of all plots is the visualization of a single function y=f(x). Here we will take a first look at creating a simple plot of this type. As with all the following sections, we'll start by setting up the notebook for plotting and importing the packages we will use:"]

^kind/void
["%matplotlib inline
  import matplotlib.pyplot as plt
  plt.style.use('seaborn-whitegrid')
  import numpy as np"]


["For all Matplotlib plots, we start by creating a figure and an axes. In their simplest form, a figure and axes can be created as follows:"]

^kind/void
["fig = plt.figure()
  ax = plt.axes()"]

^kind/vega
(hanami-common/xform
  hanami-templates/line-chart
  :WIDTH 600 :HEIGHT 500
  :ENCODINGS {:X :x
              :Y :y}
  :DATA {})

["In Matplotlib, the figure (an instance of the class plt.Figure) can be thought of as a single container that contains all the objects representing axes, graphics, text, and labels. The axes (an instance of the class plt.Axes) is what we see above: a bounding box with ticks and labels, which will eventually contain the plot elements that make up our visualization. Throughout this book, we'll commonly use the variable name fig to refer to a figure instance, and ax to refer to an axes instance or group of axes instances.

Once we have created an axes, we can use the ax.plot function to plot some data. Let's start with a simple sinusoid:"]

^kind/void
["fig = plt.figure()
  ax = plt.axes()

  x = np.linspace(0, 10, 1000)
  ax.plot(x, np.sin(x));"]

^kind/vega
(->
 (map (fn [x] {:label 0 :x x :y (dtype-func/sin (- x 0))}) x-range)
 (#(hanami-common/xform
    hanami-templates/line-chart
    :WIDTH 600 :HEIGHT 400
    :ENCODINGS {:X :x
                :Y :y}
    :DATA %)))

["Alternatively, we can use the pylab interface and let the figure and axes be created for us in the background (see Two Interfaces for the Price of One for a discussion of these two interfaces):"]


^kind/vega
(->> []
  (into (map (fn [x] {:label "sin" :x x :y (dtype-func/sin x)}) x-range))
  (into (map (fn [x] {:label "cos" :x x :y (dtype-func/cos x)}) x-range))
 (#(hanami-common/xform
    hanami-templates/line-chart
    :WIDTH 600 :HEIGHT 500
    :ENCODINGS {:X :x
                :Y :y}
    :COLOR {:field :label}
    :DATA %)))

["That's all there is to plotting simple functions in Matplotlib! We'll now dive into some more details about how to control the appearance of the axes and lines."]

["Adjusting the Plot: Line Colors and Styles

The first adjustment you might wish to make to a plot is to control the line colors and styles. The plt.plot() function takes additional arguments that can be used to specify these. To adjust the color, you can use the color keyword, which accepts a string argument representing virtually any imaginable color. The color can be specified in a variety of ways:"]


^kind/void
["plt.plot(x, np.sin(x - 0), color='blue')        # specify color by name
  plt.plot(x, np.sin(x - 1), color='g')           # short color code (rgbcmyk)
  plt.plot(x, np.sin(x - 2), color='0.75')        # Grayscale between 0 and 1
  plt.plot(x, np.sin(x - 3), color='#FFDD44')     # Hex code (RRGGBB from 00 to FF)
  plt.plot(x, np.sin(x - 4), color=(1.0,0.2,0.3)) # RGB tuple, values 0 to 1
  plt.plot(x, np.sin(x - 5), color='chartreuse'); # all HTML color names supported"]


^kind/vega
(->
 []
 (into (map (fn [x] {:x x :y (dtype-func/sin (- x 0)) :color "blue"}) x-range))
 (into (map (fn [x] {:x x :y (dtype-func/sin (- x 1)) :color "green"}) x-range))
 (into (map (fn [x] {:x x :y (dtype-func/sin (- x 2)) :color "hsl(0, 0%, 75%)"}) x-range))
 (into (map (fn [x] {:x x :y (dtype-func/sin (- x 3)) :color "#FFDD44"}) x-range))
 (into (map (fn [x] {:x x :y (dtype-func/sin (- x 4)) :color "rgb(256, 51, 77)"}) x-range))
 (into (map (fn [x] {:x x :y (dtype-func/sin (- x 5)) :color "chartreus"}) x-range))
 (#(hanami-common/xform
    hanami-templates/line-chart
    :WIDTH 600 :HEIGHT 500
    :ENCODINGS {:X :x
                :Y :y}
    :COLOR {:field :color :type "nominal" :scale nil}
    :DATA %)))

