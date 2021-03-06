import React from 'react';
import renderer from 'react-test-renderer';
import BaseField from '../../components/form-elements/BaseField';

jest.mock('redux-form', () => ({
  Field: (props) => {
    const { component: Component, name, ...others } = props;
    const input = { onChange: () => {}, name };
    const meta = {};
    return <Component input={input} meta={meta} {...others} />;
  },
}));

describe('BaseField component is correctly rendering', () => {
  it('text label', () => {
    const props = {
      fieldName: 'test',
      fieldConfig: {
        label: 'test label',
      },
      renderInput: () => (<input />),
    };

    const rendered = renderer.create(<BaseField {...props} />);

    expect(rendered.toJSON()).toMatchSnapshot();
  });

  it('component label', () => {
    const props = {
      fieldName: 'test',
      fieldConfig: {
        label: () => (<span>test</span>),
      },
      renderInput: () => (<input />),
    };

    const rendered = renderer.create(<BaseField {...props} />);

    expect(rendered.toJSON()).toMatchSnapshot();
  });

  it('attributes', () => {
    const props = {
      fieldName: 'test',
      fieldConfig: {
        label: 'test label',
        attributes: {
          required: true,
          testAttr: true,
        },
        getDynamicAttr: ({ dynamicProp }) => ({ hidden: true, dynamicAttr: dynamicProp }),
      },
      dynamicProp: 'dynamic property',
      renderInput: (input, attr) => (<input {...attr} />),
    };

    const rendered = renderer.create(<BaseField {...props} />);

    expect(rendered.toJSON()).toMatchSnapshot();
  });
});

