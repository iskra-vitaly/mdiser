## apply light conditions to 3xM array of normals

function [IMG]=applyLightCond(N, L, W)
  m = size(N, 2);
  weighted = ones(3,1)*W';
  L = normalizeVectors(L).*weighted;
  LIGHTSUM = sum(max(N'*L, 0), 2);
  minmax = [min(LIGHTSUM) max(LIGHTSUM)];
  IMG = (LIGHTSUM-minmax(1))/(minmax(2)-minmax(1));
endfunction