## Generate different illumination conditions

function [lights] = generateLight(p)
  primarySources = [
		    1  1  1;
		    1  0  1;
		    1 -1  1;
		    0  1  1;
		    0  0  1;
		    0 -1  1;
		    -1  1  1;
		    -1  0  1;
		    -1 -1  1;
		    ]';
  secondarySources = [
		      1  1  -.5;
		      1  0  -.5;
		      1 -1  -.5;
		      0  1  -.5;
		      0  0  -.5;
		      0 -1  -.5;
		      -1  1  -.5;
		      -1  0  -.5;
		      -1 -1  -.5;
		      ]';

  n = columns(primarySources);
  m = columns(secondarySources);
  lights = cell(n*m*(p*2+2), 2);
  ind = 1;
  for i = 1:n
    for j = 1:m
      L = [primarySources(:, i) secondarySources(:,j)];
      for k=p+1:-1:2
	lights(ind++, :) = {L, [k;1]};
      endfor
      lights(ind++, :) = {L, [1;1]};
      for k=2:p+1
	lights(ind++, :) = {L, [1;k]};
      endfor
      lights(ind++, :) = {primarySources(:, i), [1]};
    endfor
  endfor
end