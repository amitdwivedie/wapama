/**
 * Copyright (c) 2006
 * Martin Czuchra, Nicolas Peters, Daniel Polak, Willi Tscheschner
 * Copyright (c) 2009-2011 Intalio, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 **/

/**
 * Init namespaces
 */
if(!WAPAMA) {var WAPAMA= {};}
if(!WAPAMA.Core) {WAPAMA.Core = {};}


WAPAMA.Core.UIEnableDrag = function(event, uiObj, option) {

	this.uiObj = uiObj;
	var upL = uiObj.bounds.upperLeft();

	var a = uiObj.node.getScreenCTM();
	this.faktorXY= {x: a.a, y: a.d};
	
	this.scrollNode = uiObj.node.ownerSVGElement.parentNode.parentNode;
	
	this.offSetPosition =  {
		x: Event.pointerX(event) - (upL.x * this.faktorXY.x),
		y: Event.pointerY(event) - (upL.y * this.faktorXY.y)};

	this.offsetScroll	= {x:this.scrollNode.scrollLeft,y:this.scrollNode.scrollTop};
		
	this.dragCallback = WAPAMA.Core.UIDragCallback.bind(this);
	this.disableCallback = WAPAMA.Core.UIDisableDrag.bind(this);

	this.movedCallback = option ? option.movedCallback : undefined;
	this.upCallback = option ? option.upCallback : undefined;
	
	document.documentElement.addEventListener(WAPAMA.CONFIG.EVENT_MOUSEUP, this.disableCallback, true);
	document.documentElement.addEventListener(WAPAMA.CONFIG.EVENT_MOUSEMOVE, 	this.dragCallback , false);

};

WAPAMA.Core.UIDragCallback = function(event) {

	var position = {
		x: Event.pointerX(event) - this.offSetPosition.x,
		y: Event.pointerY(event) - this.offSetPosition.y}

	position.x 	-= this.offsetScroll.x - this.scrollNode.scrollLeft; 
	position.y 	-= this.offsetScroll.y - this.scrollNode.scrollTop;

	position.x /= this.faktorXY.x;
	position.y /= this.faktorXY.y;

	this.uiObj.bounds.moveTo(position);
	//this.uiObj.update();

	if(this.movedCallback)
		this.movedCallback(event);
	
	Event.stop(event);

};

WAPAMA.Core.UIDisableDrag = function(event) {
	document.documentElement.removeEventListener(WAPAMA.CONFIG.EVENT_MOUSEMOVE, this.dragCallback, false);
	document.documentElement.removeEventListener(WAPAMA.CONFIG.EVENT_MOUSEUP, this.disableCallback, true);
	
	if(this.upCallback)
		this.upCallback(event);
		
	this.upCallback = undefined;
	this.movedCallback = undefined;		
	
	Event.stop(event);	
};
