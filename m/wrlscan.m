function [V, M] = wrlscan(filename) 

doc = xmlread(filename);
WRL = doc.getDocumentElement;

pointsCol = WRL.getElementsByTagName('point');

V = [];

if (pointsCol.getLength > 0)
    pointEl = pointsCol.item(0);
    text = char(pointEl.getFirstChild.getData);
    V = eval(strcat('[',text,']')); 
end

meshCol = WRL.getElementsByTagName('coordIndex');

M = [];
if (meshCol.getLength > 0)
    meshEl = meshCol.item(0);
    text = char(meshEl.getFirstChild.getData);
    M = eval(strcat('[', text, ']')) + 1;
end
