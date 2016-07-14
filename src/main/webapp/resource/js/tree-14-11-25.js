var labelType, useGradients, nativeTextSupport, animate;
var nid = 0;//node的id，自动增加

var changeNode = {};
var changeStatus = {};
var heights = {};
(function () {
    var ua = navigator.userAgent,
        iStuff = ua.match(/iPhone/i) || ua.match(/iPad/i),
        typeOfCanvas = typeof HTMLCanvasElement,
        nativeCanvasSupport = (typeOfCanvas == 'object' || typeOfCanvas == 'function'),
        textSupport = nativeCanvasSupport
            && (typeof document.createElement('canvas').getContext('2d').fillText == 'function');
    //I'm setting this based on the fact that ExCanvas provides text support for IE
    //and that as of today iPhone/iPad current text support is lame
    labelType = (!nativeCanvasSupport || (textSupport && !iStuff)) ? 'Native' : 'HTML';
    nativeTextSupport = labelType == 'Native';
    useGradients = nativeCanvasSupport;
    animate = !(iStuff || !nativeCanvasSupport);
})();

var Log = {
    elem: false,
    write: function (text) {
        if (!this.elem)
            this.elem = document.getElementById('log');
        this.elem.innerHTML = text;
        this.elem.style.left = (500 - this.elem.offsetWidth / 2) + 'px';
    }
};

function queryAppConsumers(group, app) {
    var value;
    $.ajax({
        data: {
            group: group,
            app: app
        },
        url: "/consumer/showAppConsumers",
        type : "POST",
        async: false,
        success: function(result) {
            if (result.status == 0) {
                value = result.data;
            } else {
                alert("发生错误：" + result.message)
                value = [];
            }

        },
        error: function() {
            alert("发生错误");
            value = [];
        }
    });
    return value;
}

function showRelationTree(injectPlace, rootId, root, orientation, idPrefix, data, childNodeColor, func) {
    changeNode[idPrefix] = '';
    changeStatus[idPrefix] = false;
    var childArray = [];
    var init = {
        id: idPrefix + '-' + nid,
        name: '',
        data: {},
        children: []
    }
    nid++;
    childArray[0] = init;
    var hasApp = false;
    data = data.replace("[", "");
    data = data.replace("]", "");
    var child = data.split(",");
    var i = 0;
    for (i = 0; i < child.length; i++) {
        var temp = {
            id: idPrefix + '-' + nid,
            name: child[i].trim(),
            data: {},
            children: []
        };
        nid++;
        childArray[i] = temp;
    }

    var json = {
        id: rootId,
        name: root,
        data: [],
        children: childArray
    };
    var parentId = "#" + injectPlace + "-canvaswidget";
    var canvasId = "#" + injectPlace + "-canvas";
    var canvasHeight = (childArray.length + 4) * 60;
    heights[idPrefix] = canvasHeight;
    var offsetX = 350;
    if (orientation == 'right') {
        offsetX = -1 * offsetX;
    }
    var st = new $jit.ST({
        orientation: orientation,
        align: 'center',
        //id of viz container element
        injectInto: injectPlace,
        height: canvasHeight,

        //set duration for the animation
        duration: 400,
        //set animation transition type
        transition: $jit.Trans.Quart.easeInOut,
        //set distance between node and its children
        levelDistance: 150,
        levelsToShow:2,
        //set the x-offset distance from the selected node to the center of the canvas
        offsetX: offsetX,
        //enable panning
        Navigation: {
            enable: true,
            panning: true
        },
        //set node and edge styles
        //set overridable=true for styling individual
        //nodes or edges
        Node: {
            autoWidth: true,
            height: '20px',
            type: 'rectangle',
            color: '#0cf',
            align: "center",
            overridable: true
        },

        Edge: {
            type: 'bezier',
            overridable: true
        },

        onBeforeCompute: function (node) {
            // Log.write("loading " + node.name);


        },

        onAfterCompute: function () {
            // Log.write("done");
//            $(parentId).height(childArray.length*80);
//            $(canvasId).height(childArray.length*80);
        },

        //This method is called on DOM label creation.
        //Use this method to add event handlers and styles to
        //your node.
        onCreateLabel: function (label, node) {
            label.id = node.id;
            label.innerHTML = node.name;
            //set label styles
            var style = label.style;
            style.height = '30px';
            style.cursor = 'pointer';
            style.color = '#fff';
            style.fontSize = '1.1em';
            style.lineHeight = '30px';
            style.textAlign = 'left';
            style.paddingLeft = '5px';
            style.paddingTop = '8px';

            if (func != null && node._depth == 1 && node.name != '') {
                label.onclick = function () {
                    if (changeStatus[idPrefix] == false) {
                        changeStatus[idPrefix] = true;

                        // 当前点击节点已展开
                        if (changeNode[idPrefix] == node.id) {
                            st.removeSubtree(node.id, false, "replot");
                            changeNode[idPrefix] = '';
                        } else {
                            // 折叠已展开的节点
                            if (changeNode[idPrefix] != '') {
                                st.removeSubtree(changeNode[idPrefix], false, "replot");
                            }

                            // 展开自身
                            var childArray = [];
                            childArray[0] = {
                                id: idPrefix + '-' + nid++,
                                name: '',
                                data: {},
                                children: []
                            };
                            var result = func(root, node.name);
                            for (var i in result) {
                                childArray[i] = {
                                    id: idPrefix + '-' + nid++,
                                    name: result[i].hostname + ' (' + result[i].ip + ")",
                                    data: {},
                                    children: []
                                };
                            }
                            var subtree = {
                                id: node.id,
                                name: node.name,
                                data: {},
                                children: childArray
                            };
                            st.addSubtree(subtree, "replot");
                            changeNode[idPrefix] = node.id;

                            var newHeight = canvasHeight + (childArray.length * 60);
                            st.canvas.resize($(canvasId).attr("width"), newHeight);
                        }

                        changeStatus[idPrefix] = false;
                    }
                };
            }
        },

        //This method is called right before plotting
        //a node. It's useful for changing an individual node
        //style properties before plotting it.
        //The data properties prefixed with a dollar
        //sign will override the global node style properties.
        onBeforePlotNode: function (node) {
            //add some color to the nodes in the path between the
            //root node and the selected node.
            if (node.selected) {
                node.data.$color = "#c33";
            }
            else {
                delete node.data.$color;
                //if the node belongs to the last plotted level
                if (!node.anySubnode("exist")) {
                    //count children number
                    var count = 0;
                    node.eachSubnode(function (n) {
                        count++;
                    });
                    //assign a node color based on
                    //how many children it has
                    var c=['#99f'];
                    var j=0;
                    for(j=1;j<count+1;j++){
                        c[j]=childNodeColor;
                    }
                    if (hasApp) {
                        node.data.$color = c[count];
                    } else {
                        node.data.$color = [ childNodeColor , '#baa', '#caa', '#daa', '#eaa', '#faa'][count];
                    }
                }
            }
        },

        //This method is called right before plotting
        //an edge. It's useful for changing an individual edge
        //style properties before plotting it.
        //Edge data proprties prefixed with a dollar sign will
        //override the Edge global style properties.
        onBeforePlotLine: function (adj) {
            if (adj.nodeFrom.selected && adj.nodeTo.selected) {
                adj.data.$color = "#eed";
                adj.data.$lineWidth = 3;
            }
            else {
                delete adj.data.$color;
                delete adj.data.$lineWidth;
            }
        }
    });
//load json data
    st.loadJSON(json);
//compute node positions and layout
    st.compute();
//optional: make a translation of the tree
    st.geom.translate(new $jit.Complex(-200, 0), "current");
//emulate a click on the root node.
    st.onClick(st.root);
//end
//Add event handlers to switch spacetree orientation.
    var top = $jit.id('r-top'),
        left = $jit.id('r-left'),
        bottom = $jit.id('r-bottom'),
        right = $jit.id('r-right'),
        normal = $jit.id('s-normal');


    function changeHandler() {
        if (this.checked) {
            top.disabled = bottom.disabled = right.disabled = left.disabled = true;
            st.switchPosition(this.value, "animate", {
                onComplete: function () {
                    top.disabled = bottom.disabled = right.disabled = left.disabled = false;
                }
            });
        }
    };

    // top.onchange = left.onchange = bottom.onchange = right.onchange = changeHandler;
//end
}