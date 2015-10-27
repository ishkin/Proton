
/*******************************************************************************
 * Copyright 2014 IBM
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/        

dojo.provide("widgets.SectionPane");

dojo.require("dijit._Templated");
dojo.require("dijit._Widget");
dojo.require("dijit.Tooltip");
dojo.require("dojox.uuid.generateRandomUuid");
dojo.require("dojo.fx");
dojo.declare("widgets.SectionPane",
             [dijit._Widget, dijit._Templated],
{
    widgetsInTemplate: true,
    templatePath: dojo.moduleUrl("widgets","section-pane.html"),
    
    expanded: false,
    collapsable: true,
    
    expandCollapseButtonImage: null,
    collapsedViewNode: null,
    expandedViewNode: null,
	doExpandTitle: null,
	doCollapseTitle: null,
	doCollapseTooltip: null,
	doExpandTooltip:null,
	titleTooltip: null,
	initialState:"collapsed",
	
	
	setTitleTootip: function(theTip, state)
	{
		theTip = theTip.replace(/\n/g,"<br/>");
		this.titleTooltip || (this.titleTooltip = new dijit.Tooltip({label:theTip, connectId:[this.expandCollapseButtonImage.id, this.hintImageNode.id,  this.titleNode.id]}));
		if(state == "collapsed" || !state)
			this.doExpandTooltip = theTip;
		if(state == "expanded" || !state)
			this.doCollapseTooltip = theTip;
	},
    
    expandCollapse: function()
    {
        if (this.collapsable)
        {
            this.expanded = !this.expanded;
            this.displayCollapsedOrExpanded();
        }
    },
    
    expandCollapseFromTitle: function()
    {
        if(this.collapsable  &&
              (this.doExpandTitle != null || this.doCollapseTitle != null))
        {
            // expand/collapse on click in titleContainer just if it really contains title
            // and not the section content
            this.expandCollapse();
        }
    },
    
    setExpanded: function(pNewValue)
    {
        this.expanded = pNewValue;
        this.displayCollapsedOrExpanded();
    },
	
	getExpanded: function()
	{
		return  this.expanded;
	},
 
 	setTitleNodeText: function(txt)
	{
		if(this.titleNode.innerText) //IE
			this.titleNode.innerText = txt;
		else
			this.titleNode.innerHTML = txt;
	},
    
    displayCollapsedOrExpanded: function()
    {
		if (this.expanded)
        {
            this.sectionExpanded();
            this.collapsedView.style.display = 'none';
            this.expandedView.style.display = 'block';
//            dojo.fx.wipeIn({
//                node: this.expandedView,
//                duration: 300
//            }).play();
//            dojo.fx.wipeOut({
//                node: this.collapsedView,
//                duration: 300
//            }).play();
            this.expandCollapseButtonImage.src = ATVars.COLLAPSE_ICON_LOCATION;
			this.expandCollapseButtonImage.style.verticalAlign = 'middle';
			if(this.doCollapseTitle != null)
			{
				this.setTitleNodeText(this.doCollapseTitle);
			}
				
			this.refreshTitleTooltip(this.doCollapseTooltip);
            dojo.publish("SectionWasExpanded");
        }
        else
        {
            this.sectionCollapsed();
 
            this.collapsedView.style.display = 'block';
            this.expandedView.style.display = 'none';
//           dojo.fx.wipeIn({
//                node: this.collapsedView,
//                duration: 300
//            }).play();
//            dojo.fx.wipeOut({
//                node: this.expandedView,
//                duration: 300
//            }).play();
            
            this.expandCollapseButtonImage.src = ATVars.EXPAND_ICON_LOCATION;
			this.expandCollapseButtonImage.style.verticalAlign = 'text-top';
			if(this.doExpandTitle != null)
			{
				this.setTitleNodeText(this.doExpandTitle);
			}
			this.refreshTitleTooltip(this.doExpandTooltip);
        }
    },
	
	refreshTitleTooltip: function(theTip)
	{
		if(!this.titleTooltip)
			return;
		this.titleTooltip.showDelay = (theTip)? 400:Number.MAX_VALUE;
		this.titleTooltip.label = theTip;		
	},
	
    startup: function()
    {
        widgets.SectionPane.superclass.startup.apply(this, arguments);
        if (this.containerNode !== undefined && this.containerNode !== null)
        {
            var childDivs = this.containerNode.getElementsByTagName("div");
			for (var i=0; i<childDivs.length; i++)
            {
                var type = childDivs[i].getAttribute("sectionType");
				if(type === "collapsed")
				{
					this.collapsedViewNode = childDivs[i];
                    this.collapsedViewNode.parentNode.removeChild(this.collapsedViewNode);
                    i--; // The remove changes the childDivs array
                    this.collapsedView.appendChild(this.collapsedViewNode);
					var collapsedTitle =  this.collapsedViewNode.getAttribute("stateTitle");
                    var collapsedHint =  this.collapsedViewNode.getAttribute("stateHint");
					if(collapsedTitle  && collapsedTitle !== "")
                    {
						this.doExpandTitle = collapsedTitle;
                        if (collapsedHint  &&  collapsedHint !== "")
                        {
                            this.doExpandTitle += ' <span class="hint">' + collapsedHint + '</span>';
                        }
                    }
				}
				else if(type === "expanded")
				{
					this.expandedViewNode = childDivs[i];
                    this.expandedViewNode.parentNode.removeChild(this.expandedViewNode);
                    this.expandedView.appendChild(this.expandedViewNode);
                    i--; // The remove changes the childDivs array
					var expandedTitle =  this.expandedViewNode.getAttribute("stateTitle");
                    var expandedHint =  this.expandedViewNode.getAttribute("stateHint");
					if(expandedTitle  && expandedTitle !== "")
                    {
						this.doCollapseTitle = expandedTitle;
                        if (expandedHint  &&  expandedHint !== "")
                        {
                            this.doCollapseTitle += ' <span class="hint">' + expandedHint + '</span>';
                        }
                    }
				}
            }
        } 
 
		if(this.doExpandTitle == null && this.doCollapseTitle == null)
        {
			this.titleContainer.removeChild(this.titleLink);
            
            this.collapsedView.parentNode.removeChild(this.collapsedView);
            this.titleContainer.appendChild(this.collapsedView);
            this.expandedView.parentNode.removeChild(this.expandedView);
            this.titleContainer.appendChild(this.expandedView);
            
            this.imageContainerNode.style.verticalAlign = 'top';
            
            // block ability to collapse/expand the section by clicking in this TD
            dojo.removeClass(this.mainTable, "section-pane-title");
            dojo.addClass(this.imageContainerNode, "section-pane-title");
        }
		else
		{
			this.titleContainer.style.display = 'block';
            
            if (this.doExpandTitle == null)
            {
                this.doExpandTitle = this.doCollapseTitle;
            }
            else
            {
                if (this.doCollapseTitle == null)
                {
                    this.doCollapseTitle = this.doExpandTitle;
                }
            }
		}
        this.setCollapsable(this.collapsable);
        this.displayCollapsedOrExpanded();
    },
    
    setCollapsable: function(pCollapsable)
    {
        if (pCollapsable)
        {
            this.expanded = (this.initialState === "expanded");
            
            if (!this.collapsable)
            {
                dojo.removeClass(this.domNode, 'flat');
            }
        }
        else
        {
            this.expanded = true;
            
            dojo.addClass(this.domNode, 'flat');
        }
        this.collapsable = pCollapsable;
        
        this.displayCollapsedOrExpanded();
    },
    
	setTitleLinkIsVisited: function(isVisited)
	{
		this.titleLink.style.color = document.body[(isVisited?"vL":"l")+"ink"];
	},
	
	setExpandedTitle: function(newExpandedTitle)
	{
		this.doCollapseTitle = newExpandedTitle;
		 if (this.expanded)
		 	this.setTitleNodeText(this.doCollapseTitle);
	},
	
	setCollapsedTitle: function(newCollapsedTitle)
	{
		this.doExpandTitle = newCollapsedTitle;
		if (!this.expanded)
			this.setTitleNodeText(this.doExpandTitle);
	},
	
	postMixInProperties: function(){
		this.inherited("postMixInProperties", arguments);
		this._uid = dojox.uuid.generateRandomUuid();
	},
	
	setHintImage: function(hintImgSrc)
	{
		if(!hintImgSrc)
		{
			this.hintImageNode.style.display = 'none';
			this.imageContainerNode.style.width = '12px';
			return;
		}
		this.hintImageNode.style.display = 'inline';
		this.imageContainerNode.style.width = '40px';
		this.hintImageNode.src = hintImgSrc;
	},
    
	setTitle: function(newTitle)
	{
		this.setCollapsedTitle(newTitle);
		this.setExpandedTitle(newTitle);
	},
	
    sectionCollapsed: function() {},
    sectionExpanded: function() {},
    
    getCollapsedViewNode: function() { return this.collapsedViewNode;},
    getExpandedViewNode: function() { return this.expandedViewNode;}
});
